package com.qprogramming.tasq.manage;

public enum Font {
	OPEN_SANS("Open+Sans:300", "Open Sans", "sans-serif"), OPEN_SANS_CONDESED("Open+Sans+Condensed:300",
			"Open Sans Condensed", "sans-serif"), JOSEFIN_SLAB("Josefin+Slab", "Josefin Slab", "serif"), ARVO("Arvo",
					"Arvo", "serif"), MONTSERRAT("Montserrat", "Montserrat", "sans-serif"), LOBSTER("Lobster",
							"Lobster", "cursive");

	private String font;
	private String fontFamily;
	private String fontFamilyType;

	private Font(String font, String fontFamily, String fontFamilyType) {
		this.font = font;
		this.fontFamily = fontFamily;
		this.fontFamilyType = fontFamilyType;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontFamilyType() {
		return fontFamilyType;
	}

	public void setFontFamilyType(String fontFamilyType) {
		this.fontFamilyType = fontFamilyType;
	}

	public String getLink() {
		return "<link href='https://fonts.googleapis.com/css?family=" + getFont()
				+ "' rel='stylesheet' type='text/css'>";
	}

	public String getCssFamily() {
		return "font-family: '" + getFontFamily() + "', " + getFontFamilyType() + ";";
	}
}
