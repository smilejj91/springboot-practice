package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입 (값)
    private Address address;

    @JsonIgnore // 양방향 연관관계 조회
    @OneToMany(mappedBy = "member") // 난 연관관계 주인이 아님 -> mappedBy
    private List<Order> orders = new ArrayList<>();
}