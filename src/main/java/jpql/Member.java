package jpql;

import javax.persistence.*;
import static javax.persistence.FetchType.*;

@Entity
public class Member {
	@Id @GeneratedValue
	private Long id;
	private String username;
	private int age;

	@ManyToOne(fetch = LAZY)			// 다대일
	@JoinColumn(name = "TEAM_ID")		// 외래 키
	private Team team;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Team getTeam() {
		return team;
	}

	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", username='" + username + '\'' +
				", age=" + age +
				'}';
	}
}
