package jpql;

import javax.persistence.*;

@Entity
@Table(name = "ORDERS")		// "ORDER BY" 키워드로 인해, 테이블 이름 "ORDERS"로 지정
public class Order {
	@Id @GeneratedValue
	private Long id;
	private int orderAmount;

	@Embedded		// 임베디드 값 타입
	private Address address;

	@ManyToOne		// 다대일 단방향 연관관계
	@JoinColumn(name = "PRODUCT_ID")		// 외래 키
	private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
