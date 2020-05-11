package com.seattleacademy.team20;

public class Skill {


	private int id;
	private String category;
	private String name;
	private int score;

	public Skill(int id, String category, String name, int score) {
		this.id = id;
		this.category = category;
		this.name = name;
		this.score = score;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
