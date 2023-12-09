# 日本の祝日計算

祝日計算をプログラムで行う。<br/>
（ ja.osdn.net で、jholiday として公開していたのを GitHub に移動した。）

Java版とJavaScript が存在する。<br/>
１ソースのプログラムのみで構成するため、 祝日の新制度、変更が発生した場合の修正メンテナンスは１ソース内だけで行う。<br/>
JavaのJARライブラリ配布はしない。


春分・秋分の日は、『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式の結果に過ぎない。<br/>
毎年の官報公示の決定と異なったら官報公示に従うこと。

## 有効範囲
2022年以降のみをサポート対象にする。<br/>
2021年までは東京五輪によ海の日、山の日、スポーツの日が移動したことによる<br/>
計算ロジックのパフォーマンスの低下があるために、2021年までのプログラムは廃止した。<br/>

## 履歴
古いプログラムの需要に対応するために oldフォルダに古いプログラムが残っている。

