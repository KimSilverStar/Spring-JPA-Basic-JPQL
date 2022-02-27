package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain_Query {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		// persistence.xml 의 <persistence-unit name="hello">
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Member member1 = new Member();
			member1.setUsername("member1");
			member1.setAge(10);
			em.persist(member1);

			/* TypedQuery - 반환 타입이 명확한 경우 */
			TypedQuery<Member> query1 = em.createQuery(
					"select m from Member m", Member.class
			);
			TypedQuery<String> query2 = em.createQuery(
					"select m.username from Member m", String.class
			);

			/* Query - 반환 타입이 명확하지 않은 경우 */
			Query query3 = em.createQuery(
					"select m.username, m.age from Member m"
			);		// m.username: String, m.age: int

			// getResultList() - 조회 결과가 1개 이상인 경우 or 없으면 빈 리스트
			List<Member> resultList = query1.getResultList();
			for (Member member : resultList)
				System.out.println("member = " + member);

			// getSingleResult() - 조회 결과가 딱 1개인 경우 (결과가 없거나 2개 이상이면 오류)
			Member result = query1.getSingleResult();
			System.out.println("result = " + result);

			/* 파라미터 바인딩 - 이름 기준
			   (위치 기준 파라미터 바인딩은 사용하지 말 것) */
			Member singleResult = em.createQuery(
					"select m from Member m where m.username = :username", Member.class
					)
					.setParameter("username", "member1")
					.getSingleResult();
			System.out.println("singleResult = " + singleResult);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
