package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain_Function {
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
			member1.setUsername("member1");
			member1.setAge(10);
			member1.setType(MemberType.USER);
			member1.changeTeam(team);
			em.persist(member1);

			Member member2 = new Member();
			member2.setUsername(null);			// username 이 NULL !!!
			member2.setAge(60);
			member2.setType(MemberType.USER);
			member2.changeTeam(team);
			em.persist(member2);

			Member member3 = new Member();
			member3.setUsername("관리자");
			member3.setAge(25);
			member3.setType(MemberType.ADMIN);
			member3.changeTeam(team);
			em.persist(member3);

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			/* CONCAT 함수 */
			String query1 = "select concat('a', 'b') from Member m";
//			String query1 = "select 'a' || 'b' from Member m";
			List<String> resultList1 = em.createQuery(query1, String.class)
					.getResultList();
			for (String s : resultList1)
				System.out.println("s = " + s);

			/* SUBSTRING 함수 */
			String query2 = "select substring(m.username, 2, 3) from Member m";
			List<String> resultList2 = em.createQuery(query2, String.class)
					.getResultList();
			for (String s : resultList2)
				System.out.println("s = " + s);

			/* LOCATE 함수 */
			String query3 = "select locate('de', 'abcdefg') from Member m";
			List<Integer> resultList3 = em.createQuery(query3, Integer.class)
					.getResultList();
			for (Integer integer : resultList3)
				System.out.println("integer = " + integer);

			/* SIZE 함수 */
			String query4 = "select size(t.members) from Team t";
			Integer result4 = em.createQuery(query4, Integer.class)
					.getSingleResult();
			System.out.println("result4 = " + result4);

			/* 사용자 정의 함수 */
			String query5 = "select function('group_concat', m.username) from Member m";
//			String query5 = "select group_concat(m.username) from Member m";
			List<String> resultList5 = em.createQuery(query5, String.class)
					.getResultList();
			for (String s : resultList5)
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
