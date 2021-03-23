package com.ladnyik.nav.application.views.feldolgozás.entity;

public class NyomtatvanyElement implements Comparable<NyomtatvanyElement>{
	
	private String lap;
	private String kod;
	private String leiras;
	private String tipus;
	private String maxlengh;
	private String eazon;
	
	public NyomtatvanyElement(String lap, String kod, String leiras,  String tipus, String maxlengh, String eazon) {
		super();
		this.lap = lap;		
		this.kod = kod;
		this.leiras = leiras;
		this.tipus = tipus;
		this.maxlengh = maxlengh;
		this.eazon = eazon;
	}
	public String getLap() {
		return lap;
	}
	public void setLap(String lap) {
		this.lap = lap;
	}
	public String getKod() {
		return kod;
	}
	public void setKod(String kod) {
		this.kod = kod;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getMaxlengh() {
		return maxlengh;
	}
	public void setMaxlengh(String maxlengh) {
		this.maxlengh = maxlengh;
	}
	public String getEazon() {
		return eazon;
	}
	public void setEazon(String eazon) {
		this.eazon = eazon;
	}
	public String getLeiras() {
		return leiras;
	}
	public void setLeiras(String leiras) {
		this.leiras = leiras;
	}
	
	@Override
	public int compareTo(NyomtatvanyElement another) {
		
		return this.kod.compareTo(another.getKod());
		
	}	
	
}
