package jpql;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

public class JpaMain_TypeExpression {
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
			member.setType(MemberType.ADMIN);
			member.changeTeam(team);
			em.persist(member);

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			/* JPQL 타입 표현 - 문자, 숫자, Boolean, Enum, 엔티티 */
			String query1 = "select m.username, 'HELLO', true from Member m " +
							"where m.type = :userType";
			List<Object[]> resultList1 = em.createQuery(query1)
					.setParameter("userType", MemberType.ADMIN)
					.getResultList();
			for (Object[] objects : resultList1) {
				System.out.println("objects[0] = " + objects[0]);		// m.username
				System.out.println("objects[0] = " + objects[1]);		// "HELLO"
				System.out.println("objects[1] = " + objects[2]);		// true
			}

			/* SQL 표준 지원 (EXISTS, IN, AND, OR, NOT, BETWEEN, LIKE, IS NULL 등등 ...) */
			String query2 = "select m.username, 'HELLO', true from Member m " +
							"where m.age between 0 and 10";
			List<Object[]> resultList2 = em.createQuery(query2)
					.getResultList();
			for (Object[] objects : resultList2) {
				System.out.println("objects[0] = " + objects[0]);		// m.username
				System.out.println("objects[0] = " + objects[1]);		// "HELLO"
				System.out.println("objects[1] = " + objects[2]);		// true
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
