package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain_Join {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		// persistence.xml 의 <persistence-unit name="hello">
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Team team = new Team();
			team.setName("teamA");
			em.persist(team);

			Member member = new Member();
			member.setUsername("member");
			member.setAge(10);
			member.changeTeam(team);
			em.persist(member);

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			/* [Inner] Join (내부 조인) */
			String innerJoinQuery = "select m from Member m inner join m.team t";
			List<Member> resultList1 = em.createQuery(innerJoinQuery, Member.class)
					.getResultList();

			/* Left [Outer] Join (외부 조인) */
			String outerJoinQuery = "select m from Member m left outer join m.team t";
			List<Member> resultList2 = em.createQuery(outerJoinQuery, Member.class)
					.getResultList();

			/* 세타 조인 */
			String thetaJoinQuery = "select m from Member m, Team t where m.username = t.name";
			List<Member> resultList3 = em.createQuery(thetaJoinQuery, Member.class)
					.getResultList();

			/* Join 대상 필터링
			  - Member 와 Team 을 조인하는데, Team.name 이 'A'인 Team 만 조인 */
			String filteringQuery =
					"select m from Member m left outer join m.team t on t.name = 'A'";
			List<Member> resultList4 = em.createQuery(filteringQuery, Member.class)
					.getResultList();

			/* 연관관계가 없는 엔티티 내부 / 외부 조인
			  - Member.username == Team.name 인 대상 외부 조인 */
			String query =
					"select m from Member m left outer join Team t on m.username = t.name";
			List<Member> resultList5 = em.createQuery(query, Member.class)
					.getResultList();

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
