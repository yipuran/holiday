# -*- coding: utf-8 -*-
from datetime import date, timedelta
import calendar
from abc import ABCMeta, abstractmethod
import math
######################################################################
# 祝日計算
#     from holiday import Holiday
#     h = Holiday(2026)
#  月を指定して祝日 datetime.date のリストを取得： def getDates(self, month:int)->[date]:
#     list = h.getDates(9)
#  月を指定して祝日 Tuple(datetime.date, ’祝日名’) のリストを取得 : def getMonthHolidays(self, month:int)->[(date, str)]:
#     list = h.getMonthHolidays(9)
#  年間の祝日 datetime.date のリストを取得 : def listHolidayDate(self)->[date]:
#     list = h.listHolidayDate()
#  年間の祝日 Tuple(datetime.date, ’祝日名’) のリストを取得 : def listHolidays(self)->[(date, str)]:
#     list = h.listHolidays()
######################################################################
#------ 抽象クラス Holidaybundle -------
class Holidaybundle(metaclass=ABCMeta):
    # datetime.date を返す
    @abstractmethod
    def getDate(self)->date:
        pass
    # 祝日名
    @abstractmethod
    def getDescription(self)->str:
        pass
    # 振替休日
    def getChangeDate(self)->date:
        if self.getDate().weekday()==6:
            return self.getDate() + timedelta(days=self.change)
        else:
            return None
    # 振替休日存在するか？
    def hasChangeDay(self)->bool:
        return self.getDate().weekday()==6
    # 振替休日名
    def getChangeName(self)->str:
        if self.getDate().weekday() == 6:
            return "振替休日（" + self.getDescription() + "）"
        else:
            return None
    # 祝日 datetime.date
    def getHolidayDate(self)->[date]:
        return [self.getDate(), self.getChangeDate()] if self.hasChangeDay() else [self.getDate()]
    # 祝日Tupleリスト
    def getHolidays(self)->[date]:
        if self.getDate()==None: return []
        if self.hasChangeDay():
            return [(self.getDate(), self.getDescription()), (self.getChangeDate(), self.getChangeName())]
        else:
            return [(self.getDate(), self.getDescription())]
#---------- 抽象クラス : 月 bundle -----------
class Monthly(metaclass=ABCMeta):
    @abstractmethod
    def getBundles(self)->[Holidaybundle]:
        pass
    def getHolidayDate(self)->[date]:
        dateary = [v.getHolidayDate() for v in self.getBundles()]
        return [v for dateary_nest in dateary for v in dateary_nest]
    def getHolidays(self)->[(date, str)]:
        tpary = [v.getHolidays() for v in self.getBundles()]
        return [v for dateary_nest in tpary for v in dateary_nest]
#------------ Hpliday : 祝日計算クラス -------------------------------------------------------------------
class Holiday:
    def __init__(self, year=date.today().year):
        self.year = year
        self.setMonthy(year)
        pass
    # 設定されている年の取得
    def getYear(self):
        return self.year
    # 年の設定
    def setYear(self, year:int)->None:
        self.year = year
        self.setMonthy(year)
    def setMonthy(self, year:int)->None:
        self.monthly = [ self.January(year), self.February(year), self.March(year), self.April(year), self.May(year), self.June(year),
                         self.July(year), self.August(year), self.September(year), self.October(year), self.November(year), self.December(year) ]
    # 月を指定して祝日 datetime.date のリストを取得
    def getDates(self, month:int)->[date]:
        return self.monthly[month-1].getHolidayDate()
    # 月を指定して祝日 Tuple(datetime.date, ’祝日名’) のリストを取得
    def getMonthHolidays(self, month:int)->[(date, str)]:
        return self.monthly[month-1].getHolidays()
    # 年間の祝日 datetime.date のリストを取得
    def listHolidayDate(self)->[date]:
        list = [ b.getHolidayDate() for bundlelist in self.monthly for b in bundlelist.getBundles() ]
        return [ v for nest in list for v in nest ]
    # 年間の祝日 Tuple(datetime.date, ’祝日名’) のリストを取得
    def listHolidays(self)->[(date, str)]:
        list = [b.getHolidays() for bundlelist in self.monthly for b in bundlelist.getBundles()]
        return [v for nest in list for v in nest]
    class January(Monthly):
        def __init__(self, year:int):
            self.bundles = [ NewYearDay(year),  ComingOfAgeDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class February(Monthly):
        def __init__(self, year:int):
            self.bundles = [ NationalFoundationDay(year),  TennoBirthDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class March(Monthly):
        def __init__(self, year:int):
            self.bundles = [ SpringEquinoxDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class April(Monthly):
        def __init__(self, year:int):
            self.bundles = [ ShowaDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class May(Monthly):
        def __init__(self, year:int):
            self.bundles = [ KenpoukikenDay(year), MidoriDay(year), KodomoDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class June(Monthly):
        def __init__(self, year:int):
            self.bundles = []
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class July(Monthly):
        def __init__(self, year:int):
            self.bundles = [ SeaDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class August(Monthly):
        def __init__(self, year:int):
            self.bundles = [ MountainDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class September(Monthly):
        def __init__(self, year:int):
            # 国民の休日の発生は、現在９月でしかあり得ない。
            self.bundles = [ RespectForAgeDay(year), NationalHoliDay(year), AutumnEquinoxDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class October(Monthly):
        def __init__(self, year:int):
            self.bundles = [ HealthSportsDay(year) ]
        def getBundles(self)->[Holidaybundle]:
            return self.bundles
    class November(Monthly):
        def __init__(self, year: int):
            self.bundles = [ CultureDay(year), LaborThanksDay(year) ]
        def getBundles(self) -> [Holidaybundle]:
            return self.bundles
    class December(Monthly):
        def __init__(self, year: int):
            self.bundles = []
        def getBundles(self) -> [Holidaybundle]:
            return self.bundles
    # 国民の休日 list 取得
    def getNatinalHoliday(self)->[date]:
        respectForAgeDay = RespectForAgeDay(self.year)
        autumnEquinoxDay = AutumnEquinoxDay(self.year)
        aday = autumnEquinoxDay.getDate().day
        kday = respectForAgeDay.getDate().day
        if (aday - kday) == 2:
            return [date(self.year, 9, kday + 1)]
        else:
            if respectForAgeDay.hasChangeDay():
                chgday = respectForAgeDay.getChangeDate().day
                if (aday - chgday)==2:
                    return[date(self.year, 9, chgday + 1)]
            else:
                return []
# 国民の休日 Holidaybundle
class NationalHoliDay(Holidaybundle):
    def __init__(self, year:int):
        respectForAgeDay = RespectForAgeDay(year)
        autumnEquinoxDay = AutumnEquinoxDay(year)
        aday = autumnEquinoxDay.getDate().day
        kday = respectForAgeDay.getDate().day
        self.ndate = None
        if (aday - kday) == 2:
            self.ndate = date(year, 9, kday + 1)
        else:
            if respectForAgeDay.hasChangeDay():
                chgday = respectForAgeDay.getChangeDate().day
                if (aday - chgday)==2:
                    self.rdate = date(year, 9, chgday + 1)
        pass
    def getDate(self)->date:
        return self.ndate
    def getDescription(self) ->str:
        return '国民の休日'
    def hasChangeDay(self) -> bool:
        return False
    def getChangeName(self) -> str:
        return None
#---------- 祝日定義 -------------------------------------------
# 元旦 ---------------------------------------------------------
class NewYearDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 1, 1)
    def getDescription(self) ->str:
        return '元旦'
# 成人の日 ----------------------------------------------------
class ComingOfAgeDay(Holidaybundle):
    def __init__(self, year: int):
        self.year = year
        self.change = 1
    def getDate(self) -> date:
        # １月第２月曜日
        w, n = calendar.monthrange(self.year, 1)
        day = 7 * (2 - 1) + (0 - w) % 7 + 1
        return date(self.year, 1, day)
    def getDescription(self) -> str:
        return '成人の日'
# 建国記念日 -------------------------------------------------
class NationalFoundationDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 2, 11)
    def getDescription(self) ->str:
        return '建国記念日'
# 天皇誕生日 --------------------------------------------------
class TennoBirthDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 2, 23)
    def getDescription(self) ->str:
        return '天皇誕生日'
# 春分の日 ---------------------------------------------------
# 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式、1979年以前を無視、～2150年まで有効
class SpringEquinoxDay(Holidaybundle):
    def __init__(self, year: int):
        self.year = year
        self.change = 1
    def getDate(self) -> date:
        if self.year <= 2099:
            day = math.ceil(20.8431 + (0.242194 * (self.year - 1980)) - ((self.year - 1980)/4))
        else:
            day = math.ceil(21.851 + (0.242194 * (self.year - 1980)) - ((self.year - 1980)/4))
        return date(self.year, 3, day)
    def getDescription(self) -> str:
        return '春分の日'
# 昭和の日 --------------------------------------------------
class ShowaDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 4, 29)
    def getDescription(self) ->str:
        return '昭和の日'
# 憲法記念日 ------------------------------------------------
class KenpoukikenDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 3
    def getDate(self)->date:
        return date(self.year, 5, 3)
    def getDescription(self) ->str:
        return '憲法記念日'
# みどりの日 ------------------------------------------------
class MidoriDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 2
    def getDate(self)->date:
        return date(self.year, 5, 4)
    def getDescription(self) ->str:
        return 'みどりの日'
# こどもの日 ------------------------------------------------
class KodomoDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 5, 5)
    def getDescription(self) ->str:
        return 'こどもの日'
# 海の日 ------------------------------------------------
class SeaDay(Holidaybundle):
    def __init__(self, year: int):
        self.year = year
        self.change = 1
    def getDate(self) -> date:
        # ７月第3月曜日
        w, n = calendar.monthrange(self.year, 7)
        day = 7 * (3 - 1) + (0 - w) % 7 + 1
        return date(self.year, 7, day)
    def getDescription(self) -> str:
        return '海の日'
# 山の日 ------------------------------------------------
class MountainDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 8, 11)
    def getDescription(self) ->str:
        return '山の日'
# 敬老の日 ------------------------------------------------
class RespectForAgeDay(Holidaybundle):
    def __init__(self, year: int):
        self.year = year
        self.change = 1
    def getDate(self) -> date:
        # 9月第3月曜日
        w, n = calendar.monthrange(self.year, 9)
        day = 7 * (3 - 1) + (0 - w) % 7 + 1
        return date(self.year, 9, day)
    def getDescription(self) -> str:
        return '敬老の日'
# 秋分の日 ------------------------------------------------
# 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式、1979年以前を無視、～2150年まで有効
class AutumnEquinoxDay(Holidaybundle):
    def __init__(self, year: int):
        self.year = year
        self.change = 1
    def getDate(self) -> date:
        if self.year <= 2099:
            day = math.ceil(23.2488 + (0.242194 * (self.year - 1980)) - ((self.year - 1980)/4))
        else:
            day = math.ceil(24.2488 + (0.242194 * (self.year - 1980)) - ((self.year - 1980)/4))
        return date(self.year, 9, day)
    def getDescription(self) -> str:
        return '秋分の日'
# スポーツの日 -------------------------------------------
class HealthSportsDay(Holidaybundle):
    def __init__(self, year: int):
        self.year = year
        self.change = 1
    def getDate(self) -> date:
        # 10月第2月曜日
        w, n = calendar.monthrange(self.year, 10)
        day = 7 * (2 - 1) + (0 - w) % 7 + 1
        return date(self.year, 10, day)
    def getDescription(self) -> str:
        return 'スポーツの日'
# 文化の日 -----------------------------------------------
class CultureDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 11, 3)
    def getDescription(self) ->str:
        return '文化の日'
# 勤労感謝の日 -------------------------------------------
class LaborThanksDay(Holidaybundle):
    def __init__(self, year:int):
        self.year = year
        self.change = 1
    def getDate(self)->date:
        return date(self.year, 11, 23)
    def getDescription(self) ->str:
        return '勤労感謝の日'

