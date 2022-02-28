package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
import java.util.List;

public class JpaMain_Path_Expression {
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

			Member member1 = new Member();
			member1.setUsername("관리자1");
			member1.setType(MemberType.ADMIN);
			member1.changeTeam(team);
			em.persist(member1);

			Member member2 = new Member();
			member2.setUsername("관리자2");
			member2.setType(MemberType.ADMIN);
			member2.changeTeam(team);
			em.persist(member2);

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			/* 1) 상태 필드 - 경로 탐색의 끝 (더 이상 탐색 X) */
			String query1 = "select m.username from Member m";
			List<String> resultList1 = em.createQuery(query1, String.class)
							.getResultList();
			for (String s : resultList1)
				System.out.println("s = " + s);

			/* 2) 단일 값 연관 필드 - 묵시적 Inner Join 발생 (더 탐색 O) */
			String query2 = "select m.team from Member m";
			List<Team> resultList2 = em.createQuery(query2, Team.class)
					.getResultList();
			for (Team t : resultList2)
				System.out.println("t.name = " + t.getName());

			/* 단일 값 연관 필드 이후, 더 탐색 */
			String query3 = "select m.team.name from Member m";
			List<String> resultList3 = em.createQuery(query3, String.class)
					.getResultList();
			for (String s : resultList3)		// 각 Member 가 속한 Team 의 이름 출력
				System.out.println("s = " + s);

			/* 3) 컬렉션 값 연관 필드 - 묵시적 Inner Join 발생 (더 탐색 X) */
			String query4 = "select t.members from Team t";
			Collection resultList4 = em.createQuery(query4, Collection.class)
					.getResultList();			// Collection 타입으로 반환
			for (Object o : resultList4)
				System.out.println("o = " + o);

			/* 컬렉션 값 연관 필드 이후, size 등 속성 */
			String query5 = "select t.members.size from Team t";
			Integer result5 = em.createQuery(query5, Integer.class).getSingleResult();
			System.out.println("result5 = " + result5);

			/* 컬렉션 값 연관 필드
			  - From 절에서 명시적 Join 으로 별칭을 통해 더 탐색 가능 */
			// From 절에서 명시적 Join 하여, Team 의 컬렉션 값 연관 필드 members 의 별칭 m 얻음
			String query6 = "select m.username from Team t join t.members m";
			List<String> resultList6 = em.createQuery(query6, String.class)
					.getResultList();
			for (String s : resultList6)
				System.out.println("s = " + s);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
