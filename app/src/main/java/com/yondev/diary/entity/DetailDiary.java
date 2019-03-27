package com.yondev.diary.entity;


public class DetailDiary {
	private Integer code;
	private Integer diaryCode;
	private Integer type;
	private String content;
	private boolean flip;
	private Integer color;

	public String getEmoji() {
		return emoji;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	private String emoji;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getDiaryCode() {
		return diaryCode;
	}
	public void setDiaryCode(Integer diaryCode) {
		this.diaryCode = diaryCode;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public boolean isFlip() {
		return flip;
	}

	public void setFlip(boolean flip) {
		this.flip = flip;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

}
