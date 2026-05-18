package com.law.admin.service;

import com.law.admin.repository.AttorneyRepository;
import com.law.admin.repository.BannerRepository;
import com.law.admin.repository.ServiceRepository;
import com.law.admin.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 網站通用數據服務
 */
@Service
public class WebCommonService {

    private final BannerRepository bannerRepository;
    private final AttorneyRepository attorneyRepository;
    private final ServiceRepository serviceRepository;
    private final SiteRepository siteRepository;

    public WebCommonService(BannerRepository bannerRepository,
                           AttorneyRepository attorneyRepository,
                           ServiceRepository serviceRepository,
                           SiteRepository siteRepository) {
        this.bannerRepository = bannerRepository;
        this.attorneyRepository = attorneyRepository;
        this.serviceRepository = serviceRepository;
        this.siteRepository = siteRepository;
    }

    /**
     * 獲取所有輪播
     */
    public List<Map<String, Object>> getAllBanners() {
        return bannerRepository.findAllVisible();
    }

    /**
     * 獲取所有律師（完整資訊）
     */
    public List<Map<String, Object>> getAllAttorneys() {
        return attorneyRepository.findAllVisible();
    }

    /**
     * 獲取所有律師（簡化版）
     */
    public List<Map<String, Object>> getAllAttorneysSimple() {
        return attorneyRepository.findAllVisibleSimple();
    }

    /**
     * 獲取律師詳情
     */
    public Map<String, Object> getAttorneyDetail(Integer id) {
        return attorneyRepository.findVisibleById(id);
    }

    /**
     * 獲取所有業務領域
     */
    public List<Map<String, Object>> getAllServices() {
        return serviceRepository.findAllVisible();
    }

    /**
     * 獲取網站設定
     */
    public Map<String, String> getSiteSettings() {
        return siteRepository.findAllSettings();
    }
}
