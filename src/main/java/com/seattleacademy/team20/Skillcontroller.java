package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Skillcontroller {

	private static final Logger logger = LoggerFactory.getLogger(Skillcontroller.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

//		MySQLとの接続する
	/**
	 * Simply selects the home view to render by returning its name.
	 * @throws IOException
	 */
	@RequestMapping(value = "/skillUpload", method = RequestMethod.GET)
	public String skillUpload(Locale locale, Model model) {
		logger.info("Welcome ! The client locale is {}.", locale);

		try {
			initialize();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		;
		List<Skill> skills = selectSkills();

		uploadSkill(skills);
		return "skillUpload";
	}

	public List<Skill> selectSkills() {
		final String sql = "select * from skills";
		return jdbcTemplate.query(sql, new RowMapper<Skill>() {
			public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Skill(rs.getInt("id"), rs.getString("category"),
						rs.getString("name"), rs.getInt("score"));

			}
		});
	}
//		管理者権限？
	private FirebaseApp app;
//		SDKの初期化
	public void initialize() throws IOException {
		FileInputStream refreshToken = new FileInputStream(
				"/Users/ozawakouta/seattle-key/devportfolio-1248-firebase-adminsdk-pxhld-264ab13c87.json");
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(refreshToken))
				.setDatabaseUrl("https://devportfolio-1248.firebaseio.com")
				.build();
		app = FirebaseApp.initializeApp(options, "other");
	}

	public void uploadSkill(List<Skill> skills) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance(app);
		DatabaseReference ref = database.getReference("skillCategories");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		Map<String, List<Skill>> skillMap = skills.stream().collect(Collectors.groupingBy(Skill::getCategory));
		for (Map.Entry<String, List<Skill>> entry : skillMap.entrySet()) {
			//		    System.out.println(entry.getKey());
			//		    System.out.println(entry.getValue());
			map = new HashMap<>();
			map.put("category", entry.getKey());
			map.put("skills", entry.getValue());

			dataList.add(map);
		}
//			リアルタイムベース更新
		ref.setValue(dataList, new DatabaseReference.CompletionListener() {
			@Override
			public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
				if (databaseError != null) {
					System.out.println("Data could be saved" + databaseError.getMessage());
				} else {
					System.out.println("Data save successfuly");
				}
			}
		});
	}
}
