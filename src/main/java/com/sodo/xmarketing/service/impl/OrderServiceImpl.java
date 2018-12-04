/**
 *
 */
package com.sodo.xmarketing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.api.OrderLivestreamFacebookAPI;
import com.sodo.xmarketing.api.model.OrderLivestreamFacebook;
import com.sodo.xmarketing.api.model.ResponseModel;
import com.sodo.xmarketing.dto.OrderDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.BlockCulture;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.OrderExcel;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.agency.OrderAgencyData;
import com.sodo.xmarketing.model.agency.OrderLiveStreamModel;
import com.sodo.xmarketing.model.agency.OrderModel;
import com.sodo.xmarketing.model.agency.UserModel;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TargetObject;
import com.sodo.xmarketing.model.wallet.TargetObjectType;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.model.wallet.Wallet;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.OrderRepository;
import com.sodo.xmarketing.repository.wallet.WalletDeterminantRepository;
import com.sodo.xmarketing.service.CustomerService;
import com.sodo.xmarketing.service.OrderService;
import com.sodo.xmarketing.service.ServicePriceService;
import com.sodo.xmarketing.service.WalletTransactionService;
import com.sodo.xmarketing.service.agency.ConvertService;
import com.sodo.xmarketing.status.CustomerStatus;
import com.sodo.xmarketing.status.OrderStatus;
import com.sodo.xmarketing.status.SystemDeterminant;
import com.sodo.xmarketing.utils.AccountEntryContent;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.FacebookUtils;
import com.sodo.xmarketing.utils.Properties;

import lombok.var;

/**
 * @author tuanhiep225
 */
@Service
public class OrderServiceImpl implements OrderService {

	private static final Log LOGGER = LogFactory.getLog(OrderServiceImpl.class);
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private WalletTransactionService walletTransactionService;

	@Autowired
	private NextSequenceService nextSequence;
	@Autowired
	private OrderLivestreamFacebookAPI orderLivestreamFacebookAPI;

	@Autowired
	private Properties properties;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ServicePriceService servicePrice;
	
	@Autowired
	private ConvertService convertService;
	
	@Autowired
	private ConfigHelper configHelper;
	
	@Autowired
	private WalletDeterminantRepository walletDeterminant;
	
	@Autowired
	private FacebookUtils faceBookUtils;

	/**
	 * @see com.sodo.xmarketing.service.OrderService#create(com.sodo.xmarketing.model
	 *      . Order)
	 */
	@Override
	public SodResult<Order> create(Order entity, CurrentUser currentUser) {

		SodResult<Order> result = new SodResult<>();
		Order rs = beforeCreateOrder(entity, currentUser);
		WalletTransaction walltetTransaction = null;
		try {

			rs = orderRepository.add(entity);
			//xử lý đơn ngoại lệ
			Collection<Order> collectionOrder = new ArrayList<>();
			collectionOrder.add(rs);
			orderLivestreamFacebook(collectionOrder);
			
			walltetTransaction = walletTransactionService.createFromOrder(entity, currentUser).isError()
					? walletTransactionService.createFromOrder(entity, currentUser).getResult()
					: null;
			result.setResult(rs);

		} catch (Exception ex) {
			result.setError(true);
			result.setResult(null);
			result.setMessage(ex.getMessage());
			LOGGER.error(ex);
		}
		return result;
	}

	/**
	 * @param entity
	 * @param currentUser
	 * @return
	 */
	private Order beforeCreateOrder(Order entity, CurrentUser currentUser) {

		if (currentUser == null) {
			LOGGER.error("Paramater 'currentUser' must not be null");
			return null;
		}

		// verify price service
		if (null == entity.getService()) {
			LOGGER.error("Filed 'service' in model Order must not be null");
			return null;
		}

		ServicePrice service = entity.getService();

		if (service.getIsException()) {
			return handleOrderException(entity, currentUser);
		}
		try {
			entity.setCode(nextSequence.genOrderCode(currentUser.getCode()));
		} catch (SodException e) {
			LOGGER.error(e);
		}
		Customer customer = customerService.findByUsername(currentUser.getUserName());
		// verify quantity
		if (entity.getQuantity() < service.getCulture().get(currentUser.getCulture()).getMiniumOrder()) {
			entity.setQuantity(service.getCulture().get(currentUser.getCulture()).getMiniumOrder());
		}

		int numberBlock = 1;
		if (entity.getQuantity()
				% service.getBlock().getCulture().get(currentUser.getCulture()).getDenominator() != 0) {
			numberBlock = entity.getQuantity()
					/ service.getBlock().getCulture().get(currentUser.getCulture()).getDenominator() + 1;
		} else {
			numberBlock = entity.getQuantity()
					/ service.getBlock().getCulture().get(currentUser.getCulture()).getDenominator();
		}
		BigDecimal totalPrice = null;
		if (CustomerStatus.VIP1.equals(customer.getAttribute())) {
			totalPrice = service.getBlock().getCulture().get(currentUser.getCulture()).getWholesalePrices()
					.multiply(BigDecimal.valueOf(numberBlock));
		} else if(CustomerStatus.NORMAL.equals(customer.getAttribute())) {
			totalPrice = service.getBlock().getCulture().get(currentUser.getCulture()).getPrice()
					.multiply(BigDecimal.valueOf(numberBlock));
		} else if(CustomerStatus.VIP2.equals(customer.getAttribute())) {
			totalPrice = service.getBlock().getCulture().get(currentUser.getCulture()).getPriceVip2()
					.multiply(BigDecimal.valueOf(numberBlock));
		}

		entity.setPrice(totalPrice);
		entity.setUsername(currentUser.getUserName());
		entity.setCreatedBy(currentUser.getUserName());
		entity.setCreatedDate(LocalDateTime.now());
		entity.setStatus(OrderStatus.NEW);
		entity.setSale(customer.getSale());
		return entity;
	}

	/**
	 * @param entity
	 * @param currentUser
	 * @return
	 */
	private Order handleOrderException(Order entity, CurrentUser currentUser) {
		ServicePrice service = entity.getService();
		if (service.getCode().equals("FLV1")) {
			// gen mã code
			try {
				entity.setCode(nextSequence.genOrderCode(currentUser.getCode()));
			} catch (SodException e) {
				LOGGER.error(e);
			}

			// verify quantity
			if (entity.getQuantity() < service.getCulture().get(currentUser.getCulture()).getMiniumOrder()) {
				entity.setQuantity(service.getCulture().get(currentUser.getCulture()).getMiniumOrder());
			}

			// verify timeOrder
			if (entity.getTimeOrder() < 30) {
				entity.setQuantity(30);
			}

			Customer customer = customerService.findByUsername(currentUser.getUserName());
			// tính bock theo số lượng
			int numberBlockQuantity = 1;
			if (entity.getQuantity()
					% service.getBlock().getCulture().get(currentUser.getCulture()).getDenominator() != 0) {
				numberBlockQuantity = entity.getQuantity()
						/ service.getBlock().getCulture().get(currentUser.getCulture()).getDenominator() + 1;
			} else {
				numberBlockQuantity = entity.getQuantity()
						/ service.getBlock().getCulture().get(currentUser.getCulture()).getDenominator();
			}

			// tính bock theo số phút order, mỗi block 30p
			int numberBlockTimeOrder = 1;
			if (entity.getTimeOrder() % 30 != 0) {
				numberBlockTimeOrder = entity.getTimeOrder() / 30 + 1;
			} else {
				numberBlockTimeOrder = entity.getTimeOrder() / 30;
			}

			BigDecimal totalPrice = null;
			if (CustomerStatus.VIP1.equals(customer.getAttribute())) {
				totalPrice = service.getBlock().getCulture().get(currentUser.getCulture()).getWholesalePrices()
						.multiply(BigDecimal.valueOf(numberBlockQuantity))
						.multiply(BigDecimal.valueOf(numberBlockTimeOrder));
			} else if (CustomerStatus.NORMAL.equals(customer.getAttribute())) {
				totalPrice = service.getBlock().getCulture().get(currentUser.getCulture()).getPrice()
						.multiply(BigDecimal.valueOf(numberBlockQuantity))
						.multiply(BigDecimal.valueOf(numberBlockTimeOrder));
			} else if (CustomerStatus.VIP2.equals(customer.getAttribute())) {
				totalPrice = service.getBlock().getCulture().get(currentUser.getCulture()).getPriceVip2()
						.multiply(BigDecimal.valueOf(numberBlockQuantity))
						.multiply(BigDecimal.valueOf(numberBlockTimeOrder));
			}

			entity.setPrice(totalPrice);
			entity.setUsername(currentUser.getUserName());
			entity.setCreatedBy(currentUser.getUserName());
			entity.setCreatedDate(LocalDateTime.now());
			entity.setStatus(OrderStatus.NEW);
			entity.setSale(customer.getSale());
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sodo.xmarketing.service.OrderService#getById(java.lang.String)
	 */
	@Override
	public SodResult<Order> getById(String id) {
		// TODO Auto-generated method stub
		SodResult<Order> result = new SodResult<>();
		if (null == id || id.isEmpty()) {
			result.setError(true);
			result.setCode("ID_NULL");
			result.setMessage("Paramater 'id' must not be null or empty !");
			LOGGER.error(result.getMessage());
			return result;
		}
		Order rs = null;
		try {
			rs = orderRepository.get(id);
			result.setResult(rs);
		} catch (Exception ex) {
			LOGGER.error(ex);
			result.setError(true);
			result.setMessage(ex.getMessage());
		}
		return result;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see com.sodo.xmarketing.service.OrderService#get(int, int)
	 */
	@Override
	public SodResult<Page<Order>> get(int page, int pageSize) {
		SodResult<Page<Order>> result = new SodResult<>();
		PageRequest pageable = PageRequest.of(page, pageSize);
		try {
			Page<Order> rs = orderRepository.getAll(pageable);
			result.setResult(rs);
		} catch (Exception e) {
			result.setError(true);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * @return true if inserted or else false
	 */
	@Override
	public SodResult<Collection<Order>> creates(Collection<Order> entities, CurrentUser currentUser) {
		SodResult<Collection<Order>> result = new SodResult<>();
		Collection<Order> rs = null;
		Customer customer = null;

		if (currentUser == null) {
			return SodResult.<Collection<Order>>builder().isError(true).message("Current user must not be null")
					.code("NULL_CURRENT_USER").build();
		}
		customer = customerService.findByUsername(currentUser.getUserName());
		if (customer == null) {
			return SodResult.<Collection<Order>>builder().isError(true).message("Can not found customer in system.")
					.code("CUSTOMER_MISSING").build();
		}

		// xử lý tính toán đơn hàng
		entities.forEach(order -> {
			order = beforeCreateOrder(order, currentUser);
		});
		// Kiểm tra số dư của khách hàng trước khi thực hiện tạo đơn
		BigDecimal priceOrders = entities.stream().map(x -> x.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (customer.getBalance().compareTo(priceOrders) < 0)
			return SodResult.<Collection<Order>>builder().isError(true).message("Balance not enough !")
					.code("BALANCE_NOT_ENOUGH").build();
		try {
			// lưu đơn hàng xuống db
			rs = orderRepository.insert(entities);

			// lưu xong thì xử lý những đơn được đánh dấu ngoại lệ, bằng cách call api

			orderLivestreamFacebook(rs);

			walletTransactionService.createFromOrder(entities, currentUser);
			result.setResult(rs);
		} catch (Exception e) {
			result.builder().isError(true).message(e.getMessage()).build();
		}

		return result;
	}

	/**
	 * @param rs
	 */
	public void orderLivestreamFacebook(Collection<Order> rs) {
		Collection<Order> success = new ArrayList<>();
		for (Order order : rs) {
			if (order.getService().getIsException()) {
				String videoId="";
				try {
					if(faceBookUtils.checkIDVideo(order.getUrl()))
						videoId = order.getUrl();
					else
						videoId = faceBookUtils.getVideoId(order.getUrl());
					LOGGER.info("IdVideo: " + videoId);
				} catch (Exception e) {
					LOGGER.info("Lỗi lấy idVideo từ url: "+ order.getUrl());
				}
				OrderLivestreamFacebook model = OrderLivestreamFacebook.builder().account_user(properties.getAccount())
						.account_pass(properties.getPass()).method(properties.getMethod()).video_id(videoId)
						.view(order.getQuantity()).minute(order.getTimeOrder()).build();
				try {
					ResponseModel result = orderLivestreamFacebookAPI.orderLivestreamFacebook(model);
					LOGGER.info(result);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}

			}
		}
		updateStatus(success, OrderStatus.COMPLETED);

	}

	public Boolean updateStatus(Collection<Order> orders, OrderStatus status) {
		return orderRepository.updateStatus(orders, status);
	}

	@Override
	public SodResult<Collection<Order>> getAll() {
		SodResult<Collection<Order>> result = new SodResult<>();
		Collection<Order> rs = null;
		try {
			rs = orderRepository.getAll();
			result.setResult(rs);
		} catch (Exception ex) {
			result.setMessage(ex.getMessage());
			result.setError(true);
			LOGGER.error(ex);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#groupByStatusAndCount()
	 */
	@Override
	public Map<String, Long> groupByStatusAndCount(String username) {
		return this.orderRepository.groupByStatusAndCount(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#filter(java.lang.String,
	 * java.lang.String, java.lang.String, org.springframework.data.domain.Pageable,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filter(String param, String keyword, PageRequest page, CurrentUser currentUser) {

		return this.orderRepository.filter(param, keyword, page, currentUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#filterForCMS(java.lang.String,
	 * java.lang.String, org.springframework.data.domain.PageRequest,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterForCMS(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser) {
		return this.orderRepository.filterForCMS(param, keyword, pageable, currentUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#receive(java.util.Collection,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> receive(Collection<Order> entities, CurrentUser currentUser) {
		return this.orderRepository.receive(entities, currentUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.OrderService#filterForMyOrder(java.lang.String,
	 * java.lang.String, org.springframework.data.domain.PageRequest,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterForMyOrder(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser) {
		return this.orderRepository.filterForMyOrder(param, keyword, pageable, currentUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#getByCode(java.lang.String)
	 */
	@Override
	public SodResult<Order> getByCode(String code) {
		SodResult<Order> result = new SodResult<>();
		if (null == code || code.isEmpty()) {
			result.setError(true);
			result.setCode("CODE_NULL");
			result.setMessage("Paramater 'code' must not be null or empty !");
			LOGGER.error(result.getMessage());
			return result;
		}
		Order rs = null;
		try {
			rs = orderRepository.getByCode(code);
			result.setResult(rs);
		} catch (Exception ex) {
			LOGGER.error(ex);
			result.setError(true);
			result.setMessage(ex.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#update(com.sodo.xmarketing.dto.
	 * OrderDTO, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Order> update(OrderDTO order, String code, CurrentUser currentUser) {
		if (code == null || code.isEmpty()) {
			return SodResult.<Order>builder().isError(true).message("Paramater code is missing !").build();
		}
		if (currentUser == null)
			return SodResult.<Order>builder().isError(true).message("Current user must not be null")
					.code("NULL_CURRENT_USER").build();

		return orderRepository.update(order, code, currentUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.OrderService#refund(com.sodo.xmarketing.dto.
	 * OrderDTO, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> refund(String code, OrderDTO entity, CurrentUser currentUser) {
		if (entity == null) {
			return SodResult.<Boolean>builder().isError(true).message("Paramater entity must not be null !").build();
		}
		if (currentUser == null)
			return SodResult.<Boolean>builder().isError(true).message("Current user must not be null")
					.code("NULL_CURRENT_USER").build();

		// update order
		SodResult<Boolean> rs = orderRepository.refund(code, entity, currentUser);

		if (null != rs.getResult() && rs.getResult()) {
			// Update balance customer

			Order order = orderRepository.getByCode(code);
			Customer customer = customerService.findByUsername(order.getUsername());

			var afterBalance = customerService
					.updateBalance(customer.getId(), entity.getRefund(), TransactionType.DEBIT).getBalance();
			customerService.updateBalanceLife(customer.getId(), entity.getRefund(), TransactionType.CREDIT)
					.getBalance();

			// Create wallet-transaction
			
		    AccountEntryContent accountEntryContent =
		            configHelper.getConfig(AccountEntryContent.class);

		    var determinant = walletDeterminant.findByCode(SystemDeterminant.ORDER_REFUND.toString());
			WalletTransaction wallet = WalletTransaction.builder().amount(entity.getRefund())
					.beforeAmount(customer.getBalance()).afterAmount(afterBalance)
					.wallet(Wallet.builder().customerCode(customer.getCode()).customerEmail(customer.getEmail())
							.customerUserName(customer.getUsername()).customerName(customer.getName()).build())
					.target(TargetObject.builder().code(code).type(TargetObjectType.ORDER).build())
					.type(TransactionType.DEBIT)
					.walletDeterminant(Determinant.builder().code(determinant.getCode()).name(determinant.getName()).treeCode(determinant.getTreeCode()).build())
					.status(TransactionStatus.COMPLETED)
					.content(String.format(accountEntryContent.getRefundCancelOrder(), code))
					.format(customer.getFormat())
					.code(nextSequence.genWalletTransactionCode()).date(LocalDate.now()).build();

			walletTransactionService.create(wallet, currentUser);
		}

		return rs;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#checkOrderFree(com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Boolean checkOrderFree(CurrentUser currentUser) {
		Integer count = orderRepository.countByUsernameAndIsDeleteIsFalse(currentUser.getUserName());
		if(count>0)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#createOrderTrial(java.util.Collection, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Order> createOrderTrial(Order entity, CurrentUser currentUser) {
		if(!checkOrderFree(currentUser)) {
			return SodResult.<Order>builder().isError(true).message("Tài khoản của bạn đã hết số lần dùng thử dịch vụ !").build();
		}
		var block = entity.getService().getBlock().getCulture();
		// chuyển giá dịch vụ về O;
		BlockCulture blockCulture = entity.getService().getBlock().getCulture().get(currentUser.getCulture());
		blockCulture.setPrice(BigDecimal.ZERO);
		blockCulture.setWholesalePrices(BigDecimal.ZERO);
		blockCulture.setPriceVip2(BigDecimal.ZERO);
		entity.getService().getBlock().getCulture().put(currentUser.getCulture(), blockCulture);
		return create(entity, currentUser);
		
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#cmsCountByStatus(java.lang.Boolean, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Map<String, Long> cmsCountByStatus(Boolean isToday, String role, CurrentUser currentUser) {
		return orderRepository.cmsCountByStatus(isToday, role, currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#cmsTurnover(java.time.LocalDate, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Map<String, BigDecimal> cmsTurnover(LocalDate dateTime, String role, CurrentUser currentUser) {
		return orderRepository.cmsTurnover(dateTime, role, currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#createForAgency(com.sodo.xmarketing.model.agency.OrderModel)
	 */
	@Override
	public SodResult<String> createForAgency(OrderModel entity) throws SodException {
		Customer customer = null;
		customer = customerService.findByUsername(entity.getUsername());
		if (customer == null) {
			LOGGER.error("ACCOUNT_MISSING, Account missing!");
			return SodResult.<String>builder().result("error").isError(true).message("Account missing !").code("ACCOUNT_MISSING")
					.build();
		}


		if (!passwordEncoder.matches(entity.getPassword(), customer.getPassword())) {
			LOGGER.error("PASSWORD_NOT_MATCH, Password not match !");
			return SodResult.<String>builder().result("error").isError(true).message("Password not match !").code("PASSWORD_NOT_MATCH")
					.build();
		}
		
		if(entity.getOrders() == null || entity.getOrders().isEmpty()) {
			LOGGER.error("ORDER_INVALID, Orders must not be null or empty !");
			return SodResult.<String>builder().result("error").isError(true).message("Orders must not be null or empty !").code("ORDER_INVALID")
					.build();
		}
		
		List<Order> orders = new ArrayList<>();
		Collection<ServicePrice> services = servicePrice.getAll();
		for(var x : entity.getOrders()) {
			ServicePrice service = services.stream().filter(y->y.getCode().equals(x.getServiceCode())).findFirst().orElse(null);
			if(service== null) {
				return SodResult.<String>builder().result("error").isError(true).message("Can not found service by code '"+x.getServiceCode()+"'").code("SERVICE_INVALID")
						.build();
			}
			if(x.getQuantity()< service.getCulture().get(customer.getFormat().getLang()).getMiniumOrder()) {
				return SodResult.<String>builder().result("error").isError(true).message("Order with quantity "+ x.getQuantity() + " is invalid. Min is "+ service.getCulture().get(customer.getFormat().getLang()).getMiniumOrder()).code("INPUT_MIN_INVALID")
						.build();
			}
			Order order = Order.builder()
					.service(service)
					.format(customer.getFormat())
					.url(x.getUrl())
					.quantity(x.getQuantity())
					.from("API")
					.build();
			orders.add(order);
		}

		CurrentUser currentUser = CurrentUser.builder()
				.code(customer.getCode())
				.isCustomer(customer.isCustomer())
				.email(customer.getEmail())
				.fullName(customer.getName())
				.userName(customer.getUsername())
				.culture(customer.getFormat().getLang())
				.build();
		var result = creates(orders,currentUser);
		if(result.isError()) {
			return SodResult.<String>builder().result("error").message(result.getMessage()).code(result.getCode()).isError(true)
					.build();
		} else 
			return SodResult.<String>builder().result("success").isError(false)
					.build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#getOneForAgency(com.sodo.xmarketing.model.agency.UserModel, java.lang.String)
	 */
	@Override
	public SodResult<OrderAgencyData> getOneForAgency(UserModel entity, String orderCode) {
		Customer customer = null;
		customer = customerService.findByUsername(entity.getUsername());
		if (customer == null) {
			LOGGER.error("ACCOUNT_MISSING, Account missing!");
			return SodResult.<OrderAgencyData>builder().isError(true).message("Account missing !").code("ACCOUNT_MISSING")
					.build();
		}


		if (!passwordEncoder.matches(entity.getPassword(), customer.getPassword())) {
			LOGGER.error("PASSWORD_NOT_MATCH, Password not match !");
			return SodResult.<OrderAgencyData>builder().isError(true).message("Password not match !").code("PASSWORD_NOT_MATCH")
					.build();
		}
		
		OrderAgencyData result = convertService.convertOrder( orderRepository.getByCode(orderCode), customer.getAttribute().toString(), customer.getFormat().getLang());
		
		return SodResult.<OrderAgencyData>builder().isError(false).result(result)
				.build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#getAllForAgency(com.sodo.xmarketing.model.agency.UserModel)
	 */
	@Override
	public SodResult<Collection<OrderAgencyData>> getAllForAgency(UserModel entity) {
		Customer customer = null;
		customer = customerService.findByUsername(entity.getUsername());
		if (customer == null) {
			LOGGER.error("ACCOUNT_MISSING, Account missing!");
			return SodResult.<Collection<OrderAgencyData>>builder().isError(true).message("Account missing !").code("ACCOUNT_MISSING")
					.build();
		}


		if (!passwordEncoder.matches(entity.getPassword(), customer.getPassword())) {
			LOGGER.error("PASSWORD_NOT_MATCH, Password not match !");
			return SodResult.<Collection<OrderAgencyData>>builder().isError(true).message("Password not match !").code("PASSWORD_NOT_MATCH")
					.build();
		}
		var level = customer.getAttribute().toString();
		var culture = customer.getFormat().getLang();
		var orders = orderRepository.findByUsernameAndIsDeleteIsFalse(customer.getUsername());
		var result = orders.stream().map(x-> convertService.convertOrder(x, level, culture)).collect(Collectors.toList());
		
		return SodResult.<Collection<OrderAgencyData>>builder().isError(false).result(result)
				.build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#filterV2(java.lang.String, java.lang.String, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String)
	 */
	@Override
	public SodSearchResult<Order> filterV2(String param, String keyword, PageRequest pageable, CurrentUser currentUser,
			String role) {
		if(currentUser == null) {
			return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();

		} else if(!currentUser.isCustomer() && role == null)
			return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
		return orderRepository.filterV2(param, keyword, pageable, currentUser, role);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#filterV3(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser) {
		return orderRepository.filterV3(orderSearch,pageable,currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#filterReceiveV3(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterReceiveV3(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		return orderRepository.filterReceiveV3(orderSearch,pageable,currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#filterForMyOrderV3(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterForMyOrderV3(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		return orderRepository.filterForMyOrderV3(orderSearch,pageable,currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#filterOrderForSales(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterOrderForSales(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		return orderRepository.filterOrderForSales(orderSearch,pageable,currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#createOrderLiveStreamForAgency(com.sodo.xmarketing.model.agency.OrderLiveStreamModel)
	 */
	@Override
	public SodResult<String> createOrderLiveStreamForAgency(OrderLiveStreamModel entity) throws SodException {

		Customer customer = null;
		customer = customerService.findByUsername(entity.getUsername());
		if (customer == null) {
			LOGGER.error("ACCOUNT_MISSING, Account missing!");
			return SodResult.<String>builder().result("error").isError(true).message("Account missing !").code("ACCOUNT_MISSING")
					.build();
		}


		if (!passwordEncoder.matches(entity.getPassword(), customer.getPassword())) {
			LOGGER.error("PASSWORD_NOT_MATCH, Password not match !");
			return SodResult.<String>builder().result("error").isError(true).message("Password not match !").code("PASSWORD_NOT_MATCH")
					.build();
		}
		
		if(entity.getOrders() == null || entity.getOrders().isEmpty()) {
			LOGGER.error("ORDER_INVALID, Orders must not be null or empty !");
			return SodResult.<String>builder().result("error").isError(true).message("Orders must not be null or empty !").code("ORDER_INVALID")
					.build();
		}
		
		List<Order> orders = new ArrayList<>();
		Collection<ServicePrice> services = servicePrice.getAll();
		for(var x : entity.getOrders()) {
			ServicePrice service = services.stream().filter(y->y.getCode().equals("FLV1")).findFirst().orElse(null);
			if(service== null) {
				return SodResult.<String>builder().result("error").isError(true).message("Can not found service by code FLV1").code("SERVICE_INVALID")
						.build();
			}
			if(x.getQuantity()< service.getCulture().get(customer.getFormat().getLang()).getMiniumOrder()) {
				return SodResult.<String>builder().result("error").isError(true).message("Order with quantity "+ x.getQuantity() + " is invalid. Min is "+ service.getCulture().get(customer.getFormat().getLang()).getMiniumOrder()).code("INPUT_MIN_INVALID")
						.build();
			}
			Order order = Order.builder()
					.service(service)
					.format(customer.getFormat())
					.url(x.getUrl())
					.quantity(x.getQuantity())
					.timeOrder(x.getTimeMinute())
					.from("API")
					.build();
			orders.add(order);
		}

		CurrentUser currentUser = CurrentUser.builder()
				.code(customer.getCode())
				.isCustomer(customer.isCustomer())
				.email(customer.getEmail())
				.fullName(customer.getName())
				.userName(customer.getUsername())
				.culture(customer.getFormat().getLang())
				.build();
		var result = creates(orders,currentUser);
		if(result.isError()) {
			return SodResult.<String>builder().result("error").message(result.getMessage()).code(result.getCode()).isError(true)
					.build();
		} else 
			return SodResult.<String>builder().result("success").isError(false)
					.build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderService#filterV3ForExcel(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<OrderExcel> filterV3ForExcel(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		return orderRepository.filterV3ForExcel(orderSearch, pageable, currentUser);
	}

}
