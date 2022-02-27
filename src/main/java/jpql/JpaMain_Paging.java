package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain_Paging {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		// persistence.xml 의 <persistence-unit name="hello">
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			for (int i = 0; i < 100; i++) {
				Member member = new Member();
				member.setUsername("member" + i);
				member.setAge(i);
				em.persist(member);
			}

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			// 나이 내림차순으로 Member 조회
			List<Member> resultList = em.createQuery(
					"select m from Member m order by m.age desc", Member.class
			)
					.setFirstResult(1)		// [1] index 부터
					.setMaxResults(10)		// 10개
					.getResultList();
			System.out.println("resultList.size = " + resultList.size());
			for (Member member : resultList)
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
