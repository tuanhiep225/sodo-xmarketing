package com.sodo.xmarketing.repository.wallet;

import com.sodo.xmarketing.dto.AccountingEntryFilter;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.BaseRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Henry Do User: henrydo Date: 15/08/2018 Time: 14/55
 */
@Repository
public interface WalletTransactionRepository extends BaseRepository<WalletTransaction, String>, WalletTransactionCustomRepository {

  @Query("{'wallet.customerUserName': {$regex : '^?0$', $options: 'i'}, 'isDelete':false}")
  Page<WalletTransaction> findAllByWallet_CustomerUserName(String username, Pageable pageable);
  
  @Query("{'code':'?0','isDelete':false}")
  WalletTransaction getByCode(String code);


}
