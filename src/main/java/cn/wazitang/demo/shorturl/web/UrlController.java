package cn.wazitang.demo.shorturl.web;

import cn.wazitang.demo.shorturl.domain.UrlMapping;
import cn.wazitang.demo.shorturl.repo.UrlMappingRepo;
import cn.wazitang.demo.shorturl.service.UrlMappingService;
import cn.wazitang.demo.shorturl.utils.Base62;
import cn.wazitang.demo.shorturl.web.model.UrlDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者 zcw
 * 时间 2017/7/20 18:04
 * 描述 TODO
 */
@RestController
@RequestMapping("/api/url")
public class UrlController {

    @Autowired
    private UrlMappingRepo urlMappingRepo;

    @Autowired
    private UrlMappingService urlMappingService;

    @RequestMapping("/page")
    public Page<UrlDto> page(Pageable page) {
        return urlMappingRepo.findAll(page).map(domain -> {
            UrlDto dto = new UrlDto();
            dto.setKey(domain.getKey());
            dto.setShortUrl(domain.getShortUrl());
            dto.setSourceUrl(domain.getSourceUrl());
            return dto;
        });
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity<UrlMapping> add(String url) {
        if (!urlMappingService.checkUrl(url)) {
            return ResponseEntity.badRequest().build();
        }
        String key = Base62.generateShortUrl();
        UrlMapping domain = new UrlMapping();
        domain.setSourceUrl(url);
        domain.setShortUrl(key);
        domain.setKey(key);
        domain.setId(Base62.calDbIdByUrl(key));
        urlMappingRepo.save(domain);
        return ResponseEntity.ok(domain);
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public ResponseEntity<UrlMapping> edit(String key, String url) {
        if (!urlMappingService.checkUrl(url)) {
            return ResponseEntity.notFound().build();
        }
        UrlMapping urlMapping = urlMappingService.checkKey(key);
        if (urlMapping == null) {
            return ResponseEntity.notFound().build();
        }
        urlMapping.setSourceUrl(url);
        urlMappingRepo.save(urlMapping);
        return ResponseEntity.ok(urlMapping);
    }
}
