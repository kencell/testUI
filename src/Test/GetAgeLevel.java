package Test;

public class GetAgeLevel {
	
	public static int  agelevel(int age) {
		int agel = 0;
		if (age>=0 && age<=9) {
			agel = 0;
		}else if (age>=10 && age<=19) {
			agel = 1;
		}else if (age>=20 && age<=29) {
			agel = 2;
		}else if (age>=30 && age<=39) {
			agel = 3;
		}else if (age>=40 && age<=49) {
			agel = 4;
		}else if (age>=50 && age<=59) {
			agel = 5;
		}else if (age>=60 && age<=69) {
			agel = 6;
		}else if (age>=70 && age<=79) {
			agel = 7;
		}else if (age>=80 && age<=89) {
			agel = 8;
		}else{
			agel = 9;
		}
		return agel;
	}
}
