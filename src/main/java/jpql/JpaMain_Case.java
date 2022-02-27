package jpql;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

public class JpaMain_Case {
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

			/* 기본 CASE 식 */
			String query1 =
					"select " +
							"case when m.age <= 10 then '학생 요금' " +
							"	  when m.age >= 60 then '경로 요금' " +								"	  when m.age >= 60 then '경로 요금'" +							"	  when m.age >= 60 then '경로 요금'" +
							"	  else '일반 요금' " +
							"end " +
					"from Member m";
			List<String> resultList1 = em.createQuery(query1, String.class)
					.getResultList();

			for (String s : resultList1)
				System.out.println("s = " + s);

			/* COALESCE
			  - 1개씩 조회해서 NULL 이 아니면 그대로 반환, NULL 이면 명시한 값으로 반환 */
//			String query2 = "select coalesce(m.username, '이름없는 회원') from Member m";
			String query2 = "select coalesce(m.username, '이름없는 회원') as username from Member m";
			List<String> resultList2 = em.createQuery(query2, String.class)
					.getResultList();

			for (String s : resultList2)
				System.out.println("s = " + s);
			// member2 의 username 이 null 이므로, member2 에서 "이름없는 회원"으로 나옴

			/* NULLIF
			  - 두 값이 같으면 NULL 반환, 다르면 첫 번째 값 반환 */
//			String query3 = "select nullif(m.username, '관리자') from Member m";
			String query3 = "select nullif(m.username, '관리자') as username from Member m";
			List<String> resultList3 = em.createQuery(query3, String.class)
					.getResultList();

			for (String s : resultList3)
				System.out.println("s = " + s);		// Member.name 이 "관리자"이면 null

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
