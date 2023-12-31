package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);


        // 단거 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }
    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

        // then
    }
   @Test
   public void testQuery() throws Exception {
       // given
       Member m1 = new Member("AAA", 10);
       Member m2 = new Member("BBB", 20);
       memberRepository.save(m1);
       memberRepository.save(m2);

       // when
       List<Member> result = memberRepository.findUser("AAA", 10);

       // then
       assertThat(result.get(0)).isEqualTo(m1);

   }
   @Test
   public void findUserNameList() throws Exception {
       // given
       Member m1 = new Member("AAA", 10);
       Member m2 = new Member("BBB", 20);
       memberRepository.save(m1);
       memberRepository.save(m2);

       // when
       List<String> userNameList = memberRepository.findUserNameList();
       for (String s : userNameList) {
           System.out.println("s = " + s);
       }
       // then

   }
   @Test
   public void findMemberDto() throws Exception {

       Team team = new Team("teamA");
       teamRepository.save(team);
       // when
       // given
       Member m1 = new Member("AAA", 10);
       m1.setTeam(team);
       memberRepository.save(m1);

       List<MemberDto> memberDto = memberRepository.findMemberDto();
       // then
       for (MemberDto dto : memberDto) {
           System.out.println("dto = " + dto);
       }
   }
   @Test
   public void findByNames() throws Exception {
       // given
       Member m1 = new Member("AAA", 10);
       Member m2 = new Member("BBB", 20);
       memberRepository.save(m1);
       memberRepository.save(m2);
       // when
       List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

       // then
       for (Member byName : byNames) {
           System.out.println("byName = " + byName);

       }
   }
   @Test
   public void returnType() throws Exception {
       // given
       Member m1 = new Member("AAA", 10);
       Member m2 = new Member("BBB", 20);
       memberRepository.save(m1);
       memberRepository.save(m2);

       // when
       List<Member> aaa = memberRepository.findListByUsername("AAA");

       // then
       System.out.println(aaa);
   }

    @Test
    public void paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        int age = 10;
        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
    @Test
    public void bulkUpdate() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        // when

        int resultCount = memberRepository.bulkAgePlus(20);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);
        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("team = " + member.getTeam().getName());
        }

        // then
    }
    @Test
    public void queryHind() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

//        findMember.setUsername("member2");



        em.flush();

        // then
    }
    @Test
    public void Lock() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");


//        findMember.setUsername("member2");



        em.flush();
    }
    @Test
    public void callCustom() throws Exception {
        // given
        List<Member> result = memberRepository.findMemberCustom();
        // when

        // then
    }
    @Test
    public void projections() throws Exception {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        // when
        // given
        Member m1 = new Member("m1", 0,teamA);
        Member m2 = new Member("m2", 0,teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        List<NestClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestClosedProjections.class);

        for (NestClosedProjections usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }
        // then
    }
    @Test
    public void nativeQuery() throws Exception {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        // when
        // given
        Member m1 = new Member("m1", 0,teamA);
        Member m2 = new Member("m2", 0,teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamname());
        }
    }
}