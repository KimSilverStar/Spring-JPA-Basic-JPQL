package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain_Named_Query {
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
			member1.changeTeam(teamA);
			em.persist(member1);

			Member member2 = new Member();
			member2.setUsername("회원2");
			member2.changeTeam(teamA);
			em.persist(member2);

			Member member3 = new Member();
			member3.setUsername("회원3");
			member3.changeTeam(teamB);
			em.persist(member3);

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			/* NamedQuery */
			List<Member> resultList1 =
					em.createNamedQuery("Member.findByUsername", Member.class)
							.setParameter("username", "회원1")
							.getResultList();

			for (Member member : resultList1)
				System.out.println("member = " + member);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
