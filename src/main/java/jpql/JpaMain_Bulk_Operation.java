package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain_Bulk_Operation {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		// persistence.xml 의 <persistence-unit name="hello">
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Team teamA = new Team();
			teamA.setName("teamA");
			em.persist(teamA);

			Team teamB = new Team();
			teamB.setName("teamB");
			em.persist(teamB);

			Member member1 = new Member();
			member1.setUsername("회원1");
			member1.setAge(0);
			member1.changeTeam(teamA);
			em.persist(member1);

			Member member2 = new Member();
			member2.setUsername("회원2");
			member2.setAge(0);
			member2.changeTeam(teamA);
			em.persist(member2);

			Member member3 = new Member();
			member3.setUsername("회원3");
			member3.setAge(0);
			member3.changeTeam(teamB);
			em.persist(member3);

			/* 벌크 연산 */
			String query1 = "update Member m set m.age = 20";			// 모든 회원의 나이를 20 으로 변경
			int resultCount = em.createQuery(query1).executeUpdate();	// 벌크 연산 엔티티 수 반환
			System.out.println("resultCount = " + resultCount);
			// 쿼리 => 영속성 컨텍스트 자동으로 flush 됨
			// flush: 영속성 컨텍스를 DB 에 반영 (영속성 컨텍스트가 지워지는 것(clear)이 아님)

			/* 영속성 컨텍스트 - 벌크 연산 UPDATE 전의 Member 가 존재 */
			System.out.println("member1.age = " + member1.getAge());	// 벌크 연산 전 Member 의 age 출력
			System.out.println("member2.age = " + member2.getAge());
			System.out.println("member3.age = " + member3.getAge());

			// 영속성 컨텍스트 clear (모두 비움)
			// => 위의 member1 ~ 3 은 더 이상 사용 불가능. 새로 조회해야 함
			em.clear();

			Member findMember = em.find(Member.class, member1.getId());
			System.out.println("findMember = " + findMember);
			// 벌크 연산 UPDATE 가 반영된 Member 가 출력

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
