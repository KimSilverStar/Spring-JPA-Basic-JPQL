package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
import java.util.List;

public class JpaMain_Fetch_Join {
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

			/* N + 1 문제 발생하는 쿼리 */
//			String query1 = "select m from Member m";
//			List<Member> resultList1 = em.createQuery(query1, Member.class)
//							.getResultList();
//
//			for (Member member : resultList1) {
//				System.out.print("username = " + member.getUsername() + ", ");
//				System.out.println("teamName = " + member.getTeam().getName());		// N + 1
//				// 회원1, teamA	(SQL)
//				// 회원2, teamA (영속성 컨텍스트의 1차 캐시)
//				// 회원3, teamB (SQL)
//
//				// N + 1 문제 발생
//				// - 1: 회원을 가져오기 위한 쿼리 1번 발생
//				// - N: 회원 조회 쿼리 결과를 사용하여, 각 회원의 팀을 조회하는 쿼리 N번 발생
//			}

			/* 엔티티 Fetch Join 으로 N + 1 문제 해결 */
			String query2 = "select m from Member m join fetch m.team";
			List<Member> resultList2 = em.createQuery(query2, Member.class)
					.getResultList();

			for (Member member : resultList2) {
				System.out.print("username = " + member.getUsername() + ", ");
				System.out.println("teamName = " + member.getTeam().getName());
			}

			/* 컬렉션 Fetch Join - 일대다 연관관계의 join 은 중복 데이터 발생 가능 */
			String query3 = "select t from Team t join fetch t.members";
//			String query3 = "select t from Team t join fetch t.members where t.name = 'teamA'";
			List<Team> resultList3 = em.createQuery(query3, Team.class)
					.getResultList();

			for (Team team : resultList3) {
				// resultList3 에 teamA 가 2번 출력 => 일대다 join 으로 인한 중복 발생
				System.out.println("team = " + team.getName() +
						", members.size = " + team.getMembers().size());

				for (Member member : team.getMembers())
					System.out.println("-> member = " + member);
				System.out.println();
			}

			/* 컬렉션 Fetch Join - Distinct 로 애플리케이션에서 동일 id 엔티티 중복 제거 */
			String query4 = "select distinct t from Team t join fetch t.members";
			List<Team> resultList4 = em.createQuery(query4, Team.class)
					.getResultList();

			for (Team team : resultList4) {
				// resultList3 에 teamA 가 1번만 출력 => 중복 제거
				System.out.println("team = " + team.getName() +
						", members.size = " + team.getMembers().size());

				for (Member member : team.getMembers())
					System.out.println("-> member = " + member);
				System.out.println();
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
