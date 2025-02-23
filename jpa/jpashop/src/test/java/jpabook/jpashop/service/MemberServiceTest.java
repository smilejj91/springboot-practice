package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // 롤백을 위해서
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    // @Rollback(false) // 실제 commit(insert)를 하기 위함
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("lim");

        // when
        Long savedId = memberService.join(member);

        // then
        em.flush(); // commit
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test (expected = IllegalStateException.class) // 의도적 발생할 예외에 대해서 기입
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("lim1");
        Member member2 = new Member();
        member2.setName("lim1");

        // when
        memberService.join(member1);
        memberService.join(member2);
//        try {
//            memberService.join(member2); // 예외가 발생해야 한다.
//        } catch (IllegalStateException e) {
//            return;
//        }

        // then
        fail("에외가 발생해야 한다.");
    }

}