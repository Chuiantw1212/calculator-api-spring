package com.en_chu.calculator_api_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.mapper.DataAdminMapper;

@Component
@Profile("!test") // 確保此任務不在測試環境中執行
public class StartupDataCleanupService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupDataCleanupService.class);

    private final DataAdminMapper dataAdminMapper;

    public StartupDataCleanupService(DataAdminMapper dataAdminMapper) {
        this.dataAdminMapper = dataAdminMapper;
    }

    @Override
    @Transactional // 將所有刪除操作包在一個交易中，確保資料一致性
    public void run(ApplicationArguments args) throws Exception {
        logger.info("===== [Startup Task] Starting orphaned data cleanup... =====");

        int businessesDeleted = dataAdminMapper.deleteOrphanedBusinesses();
        if (businessesDeleted > 0) {
            logger.info("[Cleanup] Deleted {} orphaned records from 'user_businesses'.", businessesDeleted);
        }

        int creditCardsDeleted = dataAdminMapper.deleteOrphanedCreditCards();
        if (creditCardsDeleted > 0) {
            logger.info("[Cleanup] Deleted {} orphaned records from 'user_credit_cards'.", creditCardsDeleted);
        }

        int portfoliosDeleted = dataAdminMapper.deleteOrphanedPortfolios();
        if (portfoliosDeleted > 0) {
            logger.info("[Cleanup] Deleted {} orphaned records from 'user_portfolios'.", portfoliosDeleted);
        }

        int realEstatesDeleted = dataAdminMapper.deleteOrphanedRealEstates();
        if (realEstatesDeleted > 0) {
            logger.info("[Cleanup] Deleted {} orphaned records from 'user_real_estates'.", realEstatesDeleted);
        }

        logger.info("===== [Startup Task] Orphaned data cleanup finished. =====");
    }
}