package dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MyH2Dialect extends H2Dialect {
	public MyH2Dialect() {
		// 함수 등록 형식 - Dialect 클래스 참고
		registerFunction(
				"group_concat",
				new StandardSQLFunction("group_concat", StandardBasicTypes.STRING)
		);
	}
}
