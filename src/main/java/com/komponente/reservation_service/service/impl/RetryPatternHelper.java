package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.exceptions.ForbiddenException;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.user_sync_comm.dto.RankDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserDto;
import com.komponente.reservation_service.user_sync_comm.dto.UserIdDto;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class RetryPatternHelper {
    private Retry serviceRetry;

    public UserDto getUserByRetry(Long userId, RestTemplate userServiceRestTemplate) {
        return Retry.decorateSupplier(serviceRetry, () -> getUser(userId, userServiceRestTemplate)).get();
    }

    private UserDto getUser(Long userId, RestTemplate userServiceRestTemplate){
        try {
            return userServiceRestTemplate.exchange("/user/id?id="+userId, HttpMethod.GET, null, UserDto.class).getBody();

        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("User with id " + userId + " not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public void updateUserByRetry(Long userId, int days_between, RestTemplate userServiceRestTemplate){
        Retry.decorateSupplier(serviceRetry, () -> updateRentDays(userId, days_between, userServiceRestTemplate)).get();
    }

    public RankDto getRankByRetry(Long userId, RestTemplate userServiceRestTemplate) {
        return Retry.decorateSupplier(serviceRetry, () -> getRank(userId, userServiceRestTemplate)).get();
    }


    private RankDto getRank(Long userId, RestTemplate userServiceRestTemplate){
        try {
            return userServiceRestTemplate.exchange("/client/get_rank?user_id="+userId, HttpMethod.GET, null, RankDto.class).getBody();

        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("User with id " + userId + " not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    private Integer updateRentDays(Long userId, int days, RestTemplate userServiceRestTemplate){
        try {
            userServiceRestTemplate.exchange("/client/update_rent_days?user_id="+userId.toString() + "&rentDays=" + days, HttpMethod.POST, null, Integer.class).getBody();

        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public Long getUserIdByRetry(String username, RestTemplate userServiceRestTemplate){
        return Retry.decorateSupplier(serviceRetry, () -> getuUserId(username, userServiceRestTemplate)).get().getId();
    }

    private UserIdDto getuUserId(String username, RestTemplate userServiceRestTemplate){
        try {
            userServiceRestTemplate.exchange("/user/username?username="+username, HttpMethod.GET, null, UserIdDto.class).getBody();
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }

    public String getCompanyIdByRetry(Long userId, RestTemplate userServiceRestTemplate){
        return Retry.decorateSupplier(serviceRetry, () -> getCompanyId(userId, userServiceRestTemplate)).get();
    }

    private String getCompanyId(Long userId, RestTemplate userServiceRestTemplate) {
        try {
            return userServiceRestTemplate.exchange("/manager/get_company?user_id="+userId.toString(), HttpMethod.GET, null, String.class).getBody();
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NotFoundException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
            if(e.getStatusCode().equals(HttpStatus.FORBIDDEN)){
                throw new ForbiddenException("Forbidden");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }
}
