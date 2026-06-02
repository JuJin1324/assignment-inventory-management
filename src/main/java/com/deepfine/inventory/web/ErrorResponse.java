package com.deepfine.inventory.web;

/**
 * 에러 응답 본문. HTTP 상태 코드와 사람이 읽을 메시지만 싣는다. 두 매핑(404·400)에
 * 공통으로 쓰는 최소 모양 — 필요 이상으로 표준화하지 않는다.
 */
public record ErrorResponse(int status, String message) {
}
