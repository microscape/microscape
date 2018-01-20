package io.microscape.examples.feign;

import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient("titles")
public interface ServiceFeignClient {

    public String getTitle(String id);

}
