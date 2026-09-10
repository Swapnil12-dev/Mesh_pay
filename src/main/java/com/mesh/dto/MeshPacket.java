package com.mesh.dto;

public class MeshPacket {
	
	private String packetId;
	private int ttl;
	private Long createdAt;
	private String ciphertext;
	
	public String getPacketId() {
		return packetId;
	}
	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	public String getCiphertext() {
		return ciphertext;
	}
	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}
	@Override
	public String toString() {
		return "MeshPacket [packetId=" + packetId + ", ttl=" + ttl + ", createdAt=" + createdAt + ", ciphertext="
				+ ciphertext + "]";
	}
	public MeshPacket(String packetId, int ttl, Long createdAt, String ciphertext) {
		super();
		this.packetId = packetId;
		this.ttl = ttl;
		this.createdAt = createdAt;
		this.ciphertext = ciphertext;
	}
	public MeshPacket() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
