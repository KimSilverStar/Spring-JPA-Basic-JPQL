package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain_Projection {
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

			// 영속성 컨텍스트 모두 비움 => member1 이 영속성 컨텍스트에 없음
			em.flush();
			em.clear();

			/* 1. 엔티티 프로젝션 => 조회된 엔티티가 영속성 컨텍스트에 저장됨 */
			List<Member> resultList = em.createQuery(
					"select m from Member m", Member.class
			).getResultList();

			Member findMember = resultList.get(0);
			findMember.setAge(20);

			/* 2. 엔티티 프로젝션 - 다른 엔티티가 참조하는 엔티티(다른 테이블)도 조회 가능
			   => Join 쿼리 발생 */
			List<Team> resultList2 = em.createQuery(
					"select m.team from Member m", Team.class
			).getResultList();

			/* 다른 테이블을 조회(Join 쿼리 발생)하는 경우
			   => 명시적으로 Join 쿼리로 조회하는 것이 바람직 */
			List<Team> resultList3 = em.createQuery(
					"select t from Member m join m.team t", Team.class
			).getResultList();		// resultList2 와 동일한 결과

			/* 3. 임베디드 타입 프로젝션
			   => "select (임베디드 타입을 소유한 엔티티 별칭).(임베디드 타입)" */
			List<Address> resultList4 = em.createQuery(
					"select o.address from Order o", Address.class
			).getResultList();

			/* 4. 스칼라 타입 프로젝션
			   => 콤마(,)로 조회할 스칼라 값들 명시 */
			// 방법 1) Object[]
			List resultList5 = em.createQuery(
					"select distinct m.username, m.age from Member m"
			).getResultList();

			Object o = resultList5.get(0);		// username, age
			Object[] objects = (Object[]) o;	// username, age
			System.out.println("username = " + objects[0]);
			System.out.println("age = " + objects[1]);

			// 방법 2) List<Object[]>
			List<Object[]> resultList6 = em.createQuery(
					"select distinct m.username, m.age from Member m"
			).getResultList();

			Object[] objects2 = resultList6.get(0);
			System.out.println("username = " + objects2[0]);
			System.out.println("age = " + objects2[1]);

			// 방법 3) DTO
			List<MemberDTO> resultList7 = em.createQuery(
				"select new jpql.MemberDTO(m.username, m.age) from Member m",
				MemberDTO.class
			).getResultList();
			// 문자열로 DTO 생성자 호출할 때, 패키지 명시해야 함

			MemberDTO memberDTO = resultList7.get(0);
			System.out.println("memberDTO.name = " + memberDTO.getUsername());
			System.out.println("memberDTO.age = " + memberDTO.getAge());

			tx.commit();		// member1.age 가 20 으로 수정된 후, DB 에 저장됨
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}
