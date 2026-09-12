


/**
 * Represents a registered MeshPay user.
 *
 * A user is a person who has installed the MeshPay application
 * and created an account using their phone number.
 *
 * Responsibilities:
 * - Stores user profile information.
 * - Provides a unique VPA (Virtual Payment Address).
 * - Stores encrypted login credentials.
 *
 * Note:
 * Financial information such as account balance is not stored
 * in this entity. Balance management is handled separately by
 * the Account entity following banking system design principles.
 */
package com.mesh.entity;
import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(unique = true, nullable = false)
    private String vpa;
    
	@Column(nullable = false)
    private String password;
	

    public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(Long id, String name, String phone, String vpa, String password) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.vpa = vpa;
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", phone=" + phone + ", vpa=" + vpa + ", password=" + password
				+ "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}