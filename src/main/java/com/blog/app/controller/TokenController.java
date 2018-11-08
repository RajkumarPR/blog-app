package com.blog.app.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class TokenController {

    @Resource(name = "tokenStore")
    private TokenStore tokenStore;

    @GetMapping(value = "/tokens")
    public ResponseEntity<List<String>> getTokens(@RequestParam("client_id") String client_id) {
        List<String> tokenValues = new ArrayList<>();
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(client_id);
        if (tokens != null) {
            for (OAuth2AccessToken token : tokens) {
                tokenValues.add(token.getValue());
            }
        }
        return new ResponseEntity<>(tokenValues, HttpStatus.OK);
    }

    @Resource(name="tokenServices")
    private ConsumerTokenServices tokenServices;

    @RequestMapping(method = RequestMethod.POST, value = "/tokens/revoke/{tokenId:.*}")
    @ResponseBody
    public ResponseEntity<Boolean> revokeToken(@PathVariable String tokenId) {
       boolean status = tokenServices.revokeToken(tokenId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
