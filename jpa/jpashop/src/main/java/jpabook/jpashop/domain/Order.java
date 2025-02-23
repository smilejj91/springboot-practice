package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 이해 쉽게 하기 : Order (N) ManyToOne (1) Member
    @ManyToOne(fetch = LAZY)// 양방향 연관관계 -> 연관관계 "주인" (Orders나 Member 중 FK를 바꿀 녀석)을 정해야함 -> FK가 있는 Orders가 주인으로 하는게 편함
    @JoinColumn(name = "member_id") // FK id
    private Member member;

    // cascade는 orderItem도 같이 저장/삭제해줌
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) // 일대일도 fk를 두긴해야하고, 자주 access하는 곳에 두는 곳이 좋다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // default: 카멜케이스의 컬럼명이 자동으로 order_date로 설정됨
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING) // string이 국룰
    private OrderStatus status; // 주문상태 (ORDER, CANCEL)

    //==연관관계 "편의" 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this); // Member에도 넣음
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // 연관관계 세팅은 여기서만
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비지니스 로직==/
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품ㅁ은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==/

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
