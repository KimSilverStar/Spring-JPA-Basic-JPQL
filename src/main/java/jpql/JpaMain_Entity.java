package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain_Entity {
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

			/* 엔티티를 파라미터로 전달(m = :member) - 기본 키
			  => SQL) select m.* from Member m where m.id = ? */
			String query1 = "select m from Member m where m = :member";
			Member findMember1 = em.createQuery(query1, Member.class)
					.setParameter("member", member1)
					.getSingleResult();
			System.out.println("findMember1 = " + findMember1);

			/* 기본 키를 파라미터로 전달
			  => SQL) select m.* from Member m where m.id = ? */
			String query2 = "select m from Member m where m.id = :memberId";
			Member findMember2 = em.createQuery(query2, Member.class)
					.setParameter("memberId", member1.getId())
					.getSingleResult();
			System.out.println("findMember2 = " + findMember2);

			/* 엔티티를 파라미터로 전달(m.team = :team) - 외래 키
			  => SQL) select m.* from Member m where m.team_id = ? */
			String query3 = "select m from Member m where m.team = :team";
			List<Member> memberList1 = em.createQuery(query3, Member.class)
					.setParameter("team", teamA)
					.getResultList();
			for (Member member : memberList1)
				System.out.println("member = " + member);
			System.out.println();

			/* 외래 키를 파라미터로 전달
			  => SQL) select m.* from Member m where m.team_id = ? */
			String query4 = "select m from Member m where m.team.id = :team";
			List<Member> memberList2 = em.createQuery(query4, Member.class)
					.setParameter("team", teamA)
					.getResultList();
			for (Member member : memberList2)
				System.out.println("member = " + member);
			System.out.println();

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
