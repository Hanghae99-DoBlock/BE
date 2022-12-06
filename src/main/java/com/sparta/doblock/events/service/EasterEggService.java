package com.sparta.doblock.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.events.entity.Payment;
import com.sparta.doblock.events.repository.PaymentRepository;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EasterEggService {

    private final PaymentRepository paymentRepository;

    @Value("${kakao.adminKey}")
    private String adminKey;

    @Value("${kakao.paymentReadyUri}")
    private String paymentReadyUri;

    @Value("${kakao.payemntApprovalUri}")
    private String paymentApprovalUri;

    @Transactional
    public ResponseEntity<?> paymentReady(MemberDetailsImpl memberDetails) throws JsonProcessingException {

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .member(memberDetails.getMember())
                .amount(1000)
                .paycheck(false)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + adminKey);

        MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
        httpBody.add("cid", "TC0ONETIME");
        httpBody.add("partner_order_id", payment.getPaymentId());
        httpBody.add("partner_user_id", memberDetails.getMember().getNickname());
        httpBody.add("item_name", "두블럭을 사랑해주셔서 감사합니다ㅇ0ㅇ");
        httpBody.add("item_code", "TEAMLEGO");
        httpBody.add("quantity", "1");
        httpBody.add("total_amount", "5000");
        httpBody.add("tax_free_amount", "0");
        httpBody.add("approval_url", "http://localhost:8080/api/egg/approval");
        httpBody.add("cancel_url", "http://localhost:8080");
        httpBody.add("fail_url", "http://localhost:8080");

        HttpEntity<MultiValueMap<String, String>> paymentReadyRequest = new HttpEntity<>(httpBody, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(paymentReadyUri,paymentReadyRequest, String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        payment.updateTid(jsonNode.get("tid").asText());

        paymentRepository.save(payment);

        HttpHeaders redirectUri = new HttpHeaders();
        redirectUri.setLocation(URI.create(jsonNode.get("next_redirect_pc_url").asText()));

        return ResponseEntity.ok().headers(redirectUri).body("결제 준비 완료");
    }

    @Transactional
    public ResponseEntity<?> paymentApproval(String pgToken, MemberDetailsImpl memberDetails) {

        Payment payment = paymentRepository.findByMemberOrderByPostedAtDesc(memberDetails.getMember()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_PAYMENT)
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "KakaoAK " + adminKey);

        MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
        httpBody.add("cid", "TC0ONETIME");
        httpBody.add("tid", payment.getTid());
        httpBody.add("partner_order_id", payment.getPaymentId());
        httpBody.add("partner_user_id", payment.getMember().getNickname());
        httpBody.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> paymentApprovalRequest = new HttpEntity<>(httpBody, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(paymentApprovalUri, paymentApprovalRequest, String.class);

        payment.checkedPayment();

        paymentRepository.save(payment);

        return ResponseEntity.ok("후원 완료");
    }
}
