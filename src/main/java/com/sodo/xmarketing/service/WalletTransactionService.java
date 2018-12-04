package com.sodo.xmarketing.service;

import com.sodo.xmarketing.dto.AccountingEntryFilter;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import java.util.Collection;
import java.util.List;

/**
 * Created by Henry Do User: henrydo Date: 15/08/2018 Time: 14/56
 */
public interface WalletTransactionService {

    SodResult<Collection<WalletTransaction>> createFromOrder(Collection<Order> entities,
            CurrentUser curentUser);

    SodResult<WalletTransaction> createFromOrder(Order entities, CurrentUser curentUser);

    SodSearchResult<WalletTransaction> findAllByWallet(String username, int pageIndex,
            int pageSize);

    SodResult<WalletTransaction> create(WalletTransaction entity, CurrentUser curentUser);

    /**
     * @param currentUser
     * @throws SodException
     */
    void initData(CurrentUser currentUser) throws SodException;

    /**
     * 
     */
    void initAccountEntryContent();

    /**
     * @param data
     * @param page
     * @param size
     * @return
     */
    SodSearchResult<WalletTransaction> filterAccounting(AccountingEntryFilter data, int page,
            int size);

    /**
     * @param upperCase
     * @param numberRecord
     * @return
     */
    List<WalletTransaction> suggestAccountingEntryCode(String upperCase, int numberRecord);

    /**
     * @param accountingEntry
     * @param currentUser
     * @return
     */
    WalletTransaction handleBeforeCreate(WalletTransaction accountingEntry,
            CurrentUser currentUser);

    /**
     * @param accountingEntry
     * @param currentUser
     * @return
     * @throws SodException
     */
    WalletTransaction acceptPayment(WalletTransaction accountingEntry, CurrentUser currentUser)
            throws SodException;

	/**
	 * @param accountingEntry
	 * @param currentUser
	 * @return
	 * @throws SodException 
	 */
	WalletTransaction update(WalletTransaction accountingEntry, CurrentUser currentUser) throws SodException;

}
