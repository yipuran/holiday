/*
 * jholiday.js  祝日取得    ver 1.52
 */
if (typeof(JHoliday) == "undefined") JHoliday = { };

/**
 * 指定年の祝日(JSON)を取得
 * @param year
 */
JHoliday.getHolidays = function(year){
	var rtns = new Array();
	JHoliday.january(year).forEach(function(e){ rtns.push(e); });
	JHoliday.february(year).forEach(function(e){ rtns.push(e); });
	JHoliday.march(year).forEach(function(e){ rtns.push(e); });
	JHoliday.april(year).forEach(function(e){ rtns.push(e); });
	JHoliday.may(year).forEach(function(e){ rtns.push(e); });
	JHoliday.june(year).forEach(function(e){ rtns.push(e); });
	JHoliday.july(year).forEach(function(e){ rtns.push(e); });
	JHoliday.august(year).forEach(function(e){ rtns.push(e); });
	JHoliday.september(year).forEach(function(e){ rtns.push(e); });
	JHoliday.october(year).forEach(function(e){ rtns.push(e); });
	JHoliday.november(year).forEach(function(e){ rtns.push(e); });
	JHoliday.december(year).forEach(function(e){ rtns.push(e); });
	return rtns;
};

/**
 * 祝日or振替休日 Date の配列を返す。
 * @param year
 */
JHoliday.getDateArray = function(year){
	var rtns = new Array();
	JHoliday.dateArray1(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray2(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray3(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray4(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray5(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray6(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray7(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray8(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray9(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray10(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray11(year).forEach(function(e){ rtns.push(e); });
	JHoliday.dateArray12(year).forEach(function(e){ rtns.push(e); });
	return rtns;
};
/**
 * 月指定の祝日or振替休日 Date の配列を返す。
 * @param year
 * @param month ；  1 = １月
 */
JHoliday.getMonthDateArray = function(year, month){
	switch(parseInt(month)){
	case 1: return JHoliday.dateArray1(year);
	case 2: return JHoliday.dateArray2(year);
	case 3: return JHoliday.dateArray3(year);
	case 4: return JHoliday.dateArray4(year);
	case 5: return JHoliday.dateArray5(year);
	case 6: return JHoliday.dateArray6(year);
	case 7: return JHoliday.dateArray7(year);
	case 8: return JHoliday.dateArray8(year);
	case 9: return JHoliday.dateArray9(year);
	case 10: return JHoliday.dateArray10(year);
	case 11: return JHoliday.dateArray11(year);
	case 12: return JHoliday.dateArray12(year);
	}
};

/**
 * 月指定の祝日or振替休日 day 日付のみの配列を返す。
 * @param year
 * @param month ；  1 = １月
 */
JHoliday.getDays = function(year, month){
	var rtns = [];
	var ary = [];
	switch(parseInt(month)){
	case 1: ary = JHoliday.dateArray1(year); break;
	case 2: ary = JHoliday.dateArray2(year); break;
	case 3: ary = JHoliday.dateArray3(year); break;
	case 4: ary = JHoliday.dateArray4(year); break;
	case 5: ary = JHoliday.dateArray5(year); break;
	case 6: ary = JHoliday.dateArray6(year); break;
	case 7: ary = JHoliday.dateArray7(year); break;
	case 8: ary = JHoliday.dateArray8(year); break;
	case 9: ary = JHoliday.dateArray9(year); break;
	case 10: ary = JHoliday.dateArray10(year); break;
	case 11: ary = JHoliday.dateArray11(year); break;
	case 12: ary = JHoliday.dateArray12(year); break;
	}
	ary.forEach(function(e){
		rtns.push(new Date(Date.parse(e)).getDate());
	});
	return rtns;
};

/**
 * 指定する日が祝祭日かどうかを返す。（年、月、日を指定）
 * 祝祭日でない日曜は除外するものであくまでも祝祭日か？を返す。
 * @param year
 * @param month ；  1 = １月
 * @param day
 * @return 0=祝祭日でない。1=祝祭日である。
 */
JHoliday.isHoliday = function(year, month, day){
	var rtn = 0;
	var ary = [];
	switch(parseInt(month)){
	case 1: ary = JHoliday.dateArray1(year); break;
	case 2: ary = JHoliday.dateArray2(year); break;
	case 3: ary = JHoliday.dateArray3(year); break;
	case 4: ary = JHoliday.dateArray4(year); break;
	case 5: ary = JHoliday.dateArray5(year); break;
	case 6: ary = JHoliday.dateArray6(year); break;
	case 7: ary = JHoliday.dateArray7(year); break;
	case 8: ary = JHoliday.dateArray8(year); break;
	case 9: ary = JHoliday.dateArray9(year); break;
	case 10: ary = JHoliday.dateArray10(year); break;
	case 11: ary = JHoliday.dateArray11(year); break;
	case 12: ary = JHoliday.dateArray12(year); break;
	}
	ary.forEach(function(e){
		var rday = new Date(Date.parse(e)).getDate();
		if ( new Date(Date.parse(e)).getDate() == day){
			return rtn = 1;
		}
	});
	return rtn;
};
/**
 * 指定する日が祝祭日かどうかを返す。（Date型を指定）
 * 祝祭日でない日曜は除外するものであくまでも祝祭日か？を返す。
 * @param Date型
 * @return 0=祝祭日でない。1=祝祭日である。
 */
JHoliday.isHolidayDate = function(date){
	var rtn = 0;
	var ary = [];
	var year = parseInt(date.getFullYear());
	var day = parseInt(date.getDate());
	switch(parseInt(date.getMonth())){
	case 0: ary = JHoliday.dateArray1(year); break;
	case 1: ary = JHoliday.dateArray2(year); break;
	case 2: ary = JHoliday.dateArray3(year); break;
	case 3: ary = JHoliday.dateArray4(year); break;
	case 4: ary = JHoliday.dateArray5(year); break;
	case 5: ary = JHoliday.dateArray6(year); break;
	case 6: ary = JHoliday.dateArray7(year); break;
	case 7: ary = JHoliday.dateArray8(year); break;
	case 8: ary = JHoliday.dateArray9(year); break;
	case 9: ary = JHoliday.dateArray10(year); break;
	case 10: ary = JHoliday.dateArray11(year); break;
	case 11: ary = JHoliday.dateArray12(year); break;
	}
	ary.forEach(function(e){
		var rday = new Date(Date.parse(e)).getDate();
		if ( new Date(Date.parse(e)).getDate() == day){
			return rtn = 1;
		}
	});
	return rtn;
};
/**
 * 指定する日が祝祭日の場合に descriptionを返す。（年、月、日を指定）
 * 祝祭日でない日曜は除外するものであくまでも祝祭日か？を返す。
 * @param year
 * @param month ；  1 = １月
 * @param day
 * @return description
 */
JHoliday.description = function(year, month, day){
	var rtn = 0;
	var ary = [];
	switch(parseInt(month)){
	case 1: ary = JHoliday.january(year); break;
	case 2: ary = JHoliday.february(year); break;
	case 3: ary = JHoliday.march(year); break;
	case 4: ary = JHoliday.april(year); break;
	case 5: ary = JHoliday.may(year); break;
	case 6: ary = JHoliday.june(year); break;
	case 7: ary = JHoliday.july(year); break;
	case 8: ary = JHoliday.august(year); break;
	case 9: ary = JHoliday.september(year); break;
	case 10: ary = JHoliday.october(year); break;
	case 11: ary = JHoliday.november(year); break;
	case 12: ary = JHoliday.december(year); break;
	}
	var tstr = moment().year(year).month(month - 1).date(day).format("YYYY-MM-DD");
	var rtn = "";
	ary.forEach(function(e){
		if (tstr == e["date"]){
			rtn = e["description"];
		}
	});
	return rtn;
};
/**
 * 指定する日が祝祭日の場合に descriptionを返す。（Date型を指定）
 * 祝祭日でない日曜は除外するものであくまでも祝祭日か？を返す。
 * @param Date
 * @return description
 */
JHoliday.descriptionDate = function(date){
	var rtn = 0;
	var ary = [];
	var year = parseInt(date.getFullYear());
	var day = parseInt(date.getDate());
	month = parseInt(date.getMonth()+1);
	switch(month){
	case 1: ary = JHoliday.january(year); break;
	case 2: ary = JHoliday.february(year); break;
	case 3: ary = JHoliday.march(year); break;
	case 4: ary = JHoliday.april(year); break;
	case 5: ary = JHoliday.may(year); break;
	case 6: ary = JHoliday.june(year); break;
	case 7: ary = JHoliday.july(year); break;
	case 8: ary = JHoliday.august(year); break;
	case 9: ary = JHoliday.september(year); break;
	case 10: ary = JHoliday.october(year); break;
	case 11: ary = JHoliday.november(year); break;
	case 12: ary = JHoliday.december(year); break;
	}
	var tstr = moment().year(year).month(month - 1).date(day).format("YYYY-MM-DD");
	var rtn = "";
	ary.forEach(function(e){
		if (tstr == e["date"]){
			rtn = e["description"];
		}
	});
	return rtn;
};

/**
 * 月指定の祝日or振替休日 { 'date': yyyy-mm-dd, 'description': "xxx", 'week':xxx } の配列を返す。
 * @param year
 * @param month ；  1 = １月
 */
JHoliday.getMonthHolidayArray = function(year, month){
	switch(parseInt(month)){
	case 1: return JHoliday.january(year);
	case 2: return JHoliday.february(year);
	case 3: return JHoliday.march(year);
	case 4: return JHoliday.april(year);
	case 5: return JHoliday.may(year);
	case 6: return JHoliday.june(year);
	case 7: return JHoliday.july(year);
	case 8: return JHoliday.august(year);
	case 9: return JHoliday.september(year);
	case 10: return JHoliday.october(year);
	case 11: return JHoliday.november(year);
	case 12: return JHoliday.december(year);
	}
};
/* 1 月 */
JHoliday.dateArray1 = function(year){
	var ary = new Array();
	// 元旦
	var newYearDate = new Date(year, 1 - 1, 1);
	ary.push(newYearDate);
	if (newYearDate.getDay()===0){
		ary.push(new Date(year, 1 - 1, 2));
	}
	// 成人の日
	ary.push( JHoliday.getWhatDayOfWeek(year, 1, 2, 1) );
	return ary;
};
JHoliday.january = function(year){
	var newYearDate = new Date(year, 1 - 1, 1);
	var ary = [ { 'date': JHoliday.dateFormate(newYearDate), 'description': "元旦", 'week':JHoliday.dayOfweek(newYearDate) } ];
	if (newYearDate.getDay()===0){
		var chgdate = new Date(year, 1 - 1, 2);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	var comingOfAgeDate = JHoliday.getWhatDayOfWeek(year, 1, 2, 1);
	ary.push( { 'date': JHoliday.dateFormate(comingOfAgeDate), 'description': "成人の日", 'week':JHoliday.dayOfweek(comingOfAgeDate) } );
	return ary;
};

/* 2 月 */
JHoliday.dateArray2 = function(year){
	var ary = new Array();
	// 建国記念日
	var natinalFoundationDate = new Date(year, 2 - 1, 11);
	ary.push( natinalFoundationDate );
	if (natinalFoundationDate.getDay()===0){
		ary.push( new Date(year, 2 - 1, 12) );
	}
	// 天皇誕生日
	var tennoBirthDate = new Date(year, 2 - 1, 23);
	ary.push( tennoBirthDate );
	if (tennoBirthDate.getDay()===0){
		ary.push( JHoliday.addDate(tennoBirthDate, 1) );
	}
	return ary;
};
JHoliday.february = function(year){
	var natinalFoundationDate = new Date(year, 2 - 1, 11);
	var ary = [ { 'date': JHoliday.dateFormate(natinalFoundationDate), 'description': "建国記念日", 'week':JHoliday.dayOfweek(natinalFoundationDate) } ];
	if (natinalFoundationDate.getDay()===0){
		var chgdate = new Date(year, 2 - 1, 12);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	// 天皇誕生日
	var tennoBirthDate = new Date(year, 2 - 1, 23);
	ary.push( { 'date': JHoliday.dateFormate(tennoBirthDate), 'description': "天皇誕生日", 'week':JHoliday.dayOfweek(tennoBirthDate) } );
	if (tennoBirthDate.getDay()===0){
		var chgdate = JHoliday.addDate(tennoBirthDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
return ary;
};

/* 3 月 */
JHoliday.dateArray3 = function(year){
	var ary = new Array();
	// 春分の日
	var springEqDate = new Date(year, 3 - 1, JHoliday.springEquinox(year));
	ary.push( springEqDate );
	if (springEqDate.getDay()===0){
		ary.push( JHoliday.addDate(springEqDate, 1) );
	}
	return ary;
};
JHoliday.march = function(year){
	var springEqDate = new Date(year, 3 - 1, JHoliday.springEquinox(year));
	var ary = [ { 'date': JHoliday.dateFormate(springEqDate), 'description': "春分の日", 'week':JHoliday.dayOfweek(springEqDate) } ];
	if (springEqDate.getDay()===0){
		var chgdate = JHoliday.addDate(springEqDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate)  } );
	}
	return ary;
};

/* 4 月 */
JHoliday.dateArray4 = function(year){
	var ary = new Array();
	// 昭和の日
	var showaDate = new Date(year, 4 - 1, 29);
	ary.push( showaDate );
	if (showaDate.getDay()===0){
		ary.push( JHoliday.addDate(showaDate, 1) );
	}
	if (year==2019){
		var p = new Date(year, 4 - 1, 30);
		ary.push(p);
	}
	return ary;
};
JHoliday.april = function(year){
	var showaDate = new Date(year, 4 - 1, 29);
	var ary = [ { 'date': JHoliday.dateFormate(showaDate), 'description': "昭和の日", 'week':JHoliday.dayOfweek(showaDate) } ];
	if (showaDate.getDay()===0){
		var chgdate = JHoliday.addDate(showaDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	if (year==2019){
		var p = new Date(year, 4 - 1, 30);
		ary.push({ 'date':'2019-04-30', 'description':'国民の休日', 'week':JHoliday.dayOfweek(p) });
	}
	return ary;
};

/* 5 月 */
JHoliday.dateArray5 = function(year){
	var ary = new Array();
	if (year==2019){
		ary.push( new Date(year, 5 - 1, 1) );
		ary.push( new Date(year, 5 - 1, 2) );
	}
	// 憲法記念日   KenpouDate
	ary.push( new Date(year, 5 - 1, 3) );
	// みどりの日
	ary.push( new Date(year, 5 - 1, 4) );
	// こどもの日
	var kodomoDate = new Date(year, 5 - 1, 5);
	ary.push( kodomoDate );
	if (kodomoDate.getDay() < 3){
		ary.push( new Date(year, 5 - 1, 6) );
	}
	return ary;
};
JHoliday.may = function(year){
	var kenpouDate = new Date(year, 5 - 1, 3);
	var midoriDate = new Date(year, 5 - 1, 4);
	var kodomoDate = new Date(year, 5 - 1, 5);
	var ary = [ { 'date': JHoliday.dateFormate(kenpouDate), 'description': "憲法記念日", 'week':JHoliday.dayOfweek(kenpouDate) },
	            { 'date': JHoliday.dateFormate(midoriDate), 'description': "みどりの日", 'week':JHoliday.dayOfweek(midoriDate) },
	            { 'date': JHoliday.dateFormate(kodomoDate), 'description': "こどもの日", 'week':JHoliday.dayOfweek(kodomoDate) },
	          ];
	if (year==2019){
		var n = new Date(year, 5 - 1, 1);
		var p = new Date(year, 5 - 1, 2);
		ary.unshift({ 'date':'2019-05-02', 'description':'国民の休日', 'week':JHoliday.dayOfweek(p) });
		ary.unshift({ 'date':'2019-05-01', 'description':'平成の次の即位日', 'week':JHoliday.dayOfweek(n) });
	}
	if (kodomoDate.getDay() < 3){
		var chgdate = new Date(year, 5 - 1, 6);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	return ary;
};


/* 6 月 */
JHoliday.dateArray6 = function(year){
	return new Array();
};
JHoliday.june = function(year){
	return new Array();
};


/* 7 月 */
JHoliday.dateArray7 = function(year){
	var ary = new Array();
	if (year==2020){
		ary.push( new Date(year, 7 - 1, 23) );
		ary.push( new Date(year, 7 - 1, 24) );
		return ary;
	}
   if (year==2021){
      ary.push( new Date(year, 7 - 1, 22) );
      ary.push( new Date(year, 7 - 1, 23) );
      return ary;
   }
	// 海の日
	ary.push( JHoliday.getWhatDayOfWeek(year, 7, 3, 1) );
	return ary;
};
JHoliday.july = function(year){
	var ary = [ { 'date': JHoliday.dateFormate(JHoliday.getWhatDayOfWeek(year, 7, 3, 1)), 'description': "海の日", 'week':JHoliday.week[1] } ];
	return ary;
};


/* 8 月 */
JHoliday.dateArray8 = function(year){
	var ary = new Array();
	if (year==2021){
      ary.push( new Date(year, 8 - 1, 8) );
      ary.push( new Date(year, 8 - 1, 9) );
      return ary;
   }// 山の日
	var mountainDate = JHoliday.mountainDate(year);
	if (mountainDate != ""){
		ary.push( mountainDate );
		if (mountainDate.getDay()===0){
			ary.push( JHoliday.addDate(mountainDate, 1) );
		}
	}
	return ary;
};
JHoliday.august = function(year){
	if (year < 2016){
		return new Array();
	}
   if (year==2021){
      var ary = new Array();
      ary.push({ 'date':'2021-08-08', 'description':'山の日', 'week':JHoliday.week[0] });
      ary.push({ 'date':'2021-08-09', 'description':'振替休日', 'week':JHoliday.week[1] });
      return ary;
   }
	var mountainDate = JHoliday.mountainDate(year);
	var ary = [ { 'date': JHoliday.dateFormate(mountainDate), 'description': "山の日", 'week':JHoliday.dayOfweek(mountainDate) } ];
	if (mountainDate.getDay()===0){
		var chgdate = JHoliday.addDate(mountainDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	return ary;
};


/* 9 月 */
JHoliday.dateArray9 = function(year){
	var ary = new Array();
	// 敬老の日
	var respectAgeDate = JHoliday.getWhatDayOfWeek(year, 9, 3, 1);
	ary.push( respectAgeDate );
	// 秋分の日
	var autumnEQDate = new Date(year, 9 - 1, JHoliday.autumnEquinox(year));
	ary.push( autumnEQDate );
	// 国民の休日計算
	var rdate = respectAgeDate.getDate();
	var dif = autumnEQDate.getDate() - rdate;
	if (dif===2){
		var nationalDate = new Date(year, 9 - 1, rdate + 1);
		ary.push( nationalDate );
	}
	if (autumnEQDate.getDay()===0){
		ary.push( JHoliday.addDate(autumnEQDate, 1) );
	}
	return ary;
};
JHoliday.september = function(year){
	var respectAgeDate = JHoliday.getWhatDayOfWeek(year, 9, 3, 1);
	var autumnEQDate = new Date(year, 9 - 1, JHoliday.autumnEquinox(year));
	var ary = [ { 'date': JHoliday.dateFormate(respectAgeDate), 'description': "敬老の日", 'week':JHoliday.dayOfweek(respectAgeDate) } ];
	var rdate = respectAgeDate.getDate();
	var dif = autumnEQDate.getDate() - rdate;
	if (dif===2){
		var nationalDate = new Date(year, 9 - 1, rdate + 1);
		ary.push( { 'date': JHoliday.dateFormate(nationalDate), 'description': "国民の休日", 'week':JHoliday.dayOfweek(nationalDate) } );
	}
	ary.push( { 'date': JHoliday.dateFormate(autumnEQDate), 'description': "秋分の日", 'week':JHoliday.dayOfweek(autumnEQDate) } );
	if (autumnEQDate.getDay()===0){
		var chgdate = JHoliday.addDate(autumnEQDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	return ary;
};


/* 10 月 */
JHoliday.dateArray10 = function(year){
	var ary = new Array();
	// スポーツのの日
	ary.push( JHoliday.getWhatDayOfWeek(year, 10, 2, 1) );
	return ary;
};
JHoliday.october = function(year){
	var sportDate = JHoliday.getWhatDayOfWeek(year, 10, 2, 1);
	var ary = [ { 'date': JHoliday.dateFormate(sportDate), 'description': "スポーツの日", 'week':JHoliday.dayOfweek(sportDate) } ];
	return ary;
};


/* 11 月 */
JHoliday.dateArray11 = function(year){
	var ary = new Array();
	// 文化の日
	var cultureDate = new Date(year, 11 - 1, 3);
	ary.push( cultureDate );
	if (cultureDate.getDay()===0){
		ary.push( JHoliday.addDate(cultureDate, 1) );
	}
	// 勤労感謝の日
	var laborThanksDate = new Date(year, 11 - 1, 23);
	ary.push( laborThanksDate );
	if (laborThanksDate.getDay()===0){
		ary.push( JHoliday.addDate(laborThanksDate, 1) );
	}
	return ary;
};
JHoliday.november = function(year){
	var cultureDate = new Date(year, 11 - 1, 3);
	var ary = [ { 'date': JHoliday.dateFormate(cultureDate), 'description': "文化の日", 'week':JHoliday.dayOfweek(cultureDate) } ];
	if (cultureDate.getDay()===0){
		var chgdate = JHoliday.addDate(cultureDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	var laborThanksDate = new Date(year, 11 - 1, 23);
	ary.push( { 'date': JHoliday.dateFormate(laborThanksDate), 'description': "勤労感謝の日", 'week':JHoliday.dayOfweek(laborThanksDate) } );
	if (laborThanksDate.getDay()===0){
		var chgdate = JHoliday.addDate(laborThanksDate, 1);
		ary.push( { 'date': JHoliday.dateFormate(chgdate), 'description': "振替休日", 'week':JHoliday.dayOfweek(chgdate) } );
	}
	return ary;

};

/* 12 月 */
JHoliday.dateArray12 = function(year){
	if (year < 2019){
		var ary = new Array();
		// 天皇誕生日
		var tennoBirthDate = new Date(year, 12 - 1, 23);
		ary.push( tennoBirthDate );
		if (tennoBirthDate.getDay()===0){
			ary.push( JHoliday.addDate(tennoBirthDate, 1) );
		}
		return ary;
	}else{
		return new Array();
	}
};
JHoliday.december = function(year){
	return new Array();
};

// 曜日算出
JHoliday.week = new Array("日", "月", "火", "水", "木", "金", "土");
JHoliday.dayOfweek = function(date){
	return JHoliday.week[date.getDay()];
};
/*-------------------------------------------------------------------------------------------------*/
/* Date → 'yyyy-MM-dd' */
JHoliday.dateFormate = function(date){
	var m = date.getMonth() + 1;
	var d = date.getDate();
	if (m < 10){ m = '0' + m; }
	if (d < 10){ d = '0' + d; }
	return date.getFullYear() + '-' + m + '-' + d;
};
/* 任意の年月の第n曜日の日付を求める関数
 * year 年
 * month 月
 * number 何番目の曜日か、第1曜日なら1。第3曜日なら3
 * dayOfWeek 求めたい曜日。0〜6までの数字で曜日の日〜土を指定する
 */
JHoliday.getWhatDayOfWeek = function(year, month, number, dayOfWeek){
	var firstDt = new Date(year, month - 1, 1);
	var firstDayOfWeek = firstDt.getDay();//指定した年月の1日の曜日を取得
	var day = dayOfWeek - firstDayOfWeek + 1;
	if(day <= 0) day += 7;//1週間を足す
	var dt = new Date(year, month - 1, day);
	var msTime = dt.getTime();
	msTime += (86400000 * 7 * (number - 1));//n曜日まで1週間を足し込み
	dt.setTime(msTime);
	return dt;
};
/* 年月日と加算日からn日後、n日前を求める関数
 * dt  Date
 * addDays 加算日。マイナス指定でn日前も設定可能
 */
JHoliday.addDate = function(dt, addDays){
	var baseSec = dt.getTime();
	var addSec = 60 * 60 * 24 * addDays * 1000;
	var rdt = new Date();
	rdt.setTime(baseSec + addSec);
	return rdt;
};
/* 春分の日 */
JHoliday.springEquinox = function(year){
	if (year <= 2099){
		return Math.floor( (20.8431 + (0.242194 * (year - 1980)) ) - Math.floor( ((year - 1980 )/4)) );
	}
	return Math.floor( (21.851 + (0.242194 * (year - 1980)) ) - Math.floor( ((year - 1980)/4)) );
};
/* 秋分の日 */
JHoliday.autumnEquinox = function(year){
	if (year <= 2099){
		return Math.floor( (23.2488 + (0.242194 * (year - 1980)) ) - Math.floor( ((year - 1980)/4)) );
	}
	return Math.floor( (24.2488 + (0.242194 * (year - 1980)) ) - Math.floor( ((year - 1980)/4)) );
};
/* 山の日 */
JHoliday.mountainDate = function(year){
	if (year >= 2016){
		if (year==2020) return new Date(year, 8 - 1, 10);
		return new Date(year, 8 - 1, 11);
	}
	return "";
};

