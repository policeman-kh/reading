* Worrying About Your Tail (or, Instrumentation and Performance)



When building a monitoring system from scratch, it’s tempting to design a system based upon the mean of some quantity: the mean latency, the mean CPU usage of your nodes, or the mean fullness of your databases. The danger presented by the latter two cases is obvious: CPUs and databases can easily be utilized in a very imbalanced way. The same holds for latency. If you run a web service with an average latency of 100 ms at 1,000 requests per second, 1% of requests might easily take 5 seconds. If your users depend on several such web services to render their page, the 99th percentile of one backend can easily become the median response of your frontend.


スクラッチで監視システムを構築する場合、
いくつかの量の平均に基づいて設計するのが良い
・latency(待ち時間)の平均
・ノードのCPU使用率の平均
・データベースのfullness?の平均

CPU,データベースによって危険性が示されるのは明らか
CPUとデータベースを簡単に非常に不均衡な方法で利用することができます
同じことが、latencyにも当てはまります
あなたは、毎秒1,000件の要求に100ミリ秒の平均待ち時間でWebサービスを実行する場合は、
リクエストの1％は簡単に5秒かかる

ユーザーがそれらのページを参照するためにそのようなWEBサービスに左右される場合
バックエンドの 99 percentileはあなたのフロントエンドの中央値のレスポンスになることができる


The simplest way to differentiate between a slow average and a very slow “tail” of requests is to collect request counts bucketed by latencies (suitable for rendering a histogram), rather than actual latencies: how many requests did I serve that took between 0 ms and 10 ms, between 10 ms and 30 ms, between 30 ms and 100 ms, between 100 ms and 300 ms, and so on? Distributing the histogram boundaries approximately exponentially (in this case by factors of roughly 3) is often an easy way to visualize the distribution of your requests.

遅い平均との非常に遅い "tail"のリクエストを区別するシンプルな方法は
latenciesによって収集されたリクエスト数をカウントすること
(ヒストグラムをレンダリングするのに適している)
実際のlatenciesよりも
0ms - 10ms
10ms - 30ms
30ms - 100ms 
100ms - 300ms
のリクエスト数がどのくらいか？
およそ指数関数的にヒストグラムの境界を分布する

多くの場合、あなたの要求の分布を可視化するのは簡単

* Choosing an Appropriate Resolution for Measurements

測定のための適切な解決？を選択する

Different aspects of a system should be measured with different levels of granularity. For example:

システムの異なった局面は粒度の異なるレベルで測定されるべき
例えば
** Observing CPU load over the time span of a minute won’t reveal even quite long-lived spikes that drive high tail latencies.

1分ごと　CPUロード　は 瞬間的に高いlatenciesが完全に明確でないしょう

** On the other hand, for a web service targeting no more than 9 hours aggregate 
downtime per year (99.9% annual uptime), probing for a 200 (success) status more than once or twice a minute is probably unnecessarily frequent.

一方、年間9時間未満のダウンタイム(99.9% 稼働)とするWebサービスのため、
1,2分間ごとに ステータス200 を応答するか調査することは必要以上に頻繁にあります

** Similarly, checking hard drive fullness for a service targeting 99.9% availability more than once every 1–2 minutes is probably unnecessary.

同様に　毎1,2分ごとに99.9%稼働とするサービスのハードドライブをチェックすることはおそらく不要です。

Take care in how you structure the granularity of your measurements. Collecting per-second measurements of CPU load might yield interesting data, but such frequent measurements may be very expensive to collect, store, and analyze. If your monitoring goal calls for high resolution but doesn’t require extremely low latency, you can reduce these costs by performing internal sampling on the server, then configuring an external system to collect and aggregate that distribution over time or across servers. You might:

あなたがあなたの測定の精度を構造化する方法に注意してください
CPU負荷の毎秒の測定値を収集することは、興味深いデータが得られるかもしれませんが、
そのような頻繁に収集、格納、分析することは非常に高価になることもあります。

もし、あなたの監視の目標が、高い分析を求めるが遅延が低いことを要求しないのであれば
あなたは、サーバー上で内部サンプリングを行うことにより、これらのコストを削減することができます
さらに
外部システムを構成する際に収集し、時間をかけて、またはサーバー間でその分布を集計します
時間の経過またはサーバー間でその分布を収集し、集約する外部システムを構成します

** Record the current CPU utilization each second.

現在のCPU使用率を毎秒記録する

** Using buckets of 5% granularity, increment the appropriate CPU utilization bucket each second.

5％の粒度のbucketsを使用して、適切なCPU使用率のバケット　を毎秒ごとに　インクリメントする。


** Aggregate those values every minute.

毎分それらの値を集計する

This strategy allows you to observe brief CPU hotspots without incurring very high cost due to collection and retention.

この戦略は、あなたが収集および保存のために非常に高コスト化を招くことなく、簡単なCPUのhotspotsを観察することができます。

* As Simple as Possible, No Simpler

可能な限りシンプルに

Piling all these requirements on top of each other can add up to a very complex monitoring system—your system might end up with the following levels of complexity:

★★★

** Alerts on different latency thresholds, at different percentiles, on all kinds of different metrics

異なるlatencyのしきい値のアラート　異なるpercentiles　すべての種類の異なるmetrics

** Extra code to detect and expose possible causes

検出し、考えられる原因を露出するためのExtra code 

** Associated dashboards for each of these possible causes

これらの原因のそれぞれについて、関連するダッシュボード


The sources of potential complexity are never-ending. Like all software systems, monitoring can become so complex that it’s fragile, complicated to change, and a maintenance burden.

複雑な潜在するソースは終わることがない
すべてのソフトウェアシステムと同様
監視は　とても複雑になる　　それは壊れやすい、変更が複雑、保守の負担

Therefore, design your monitoring system with an eye toward simplicity. In choosing what to monitor, keep the following guidelines in mind:

したがって、単純化に向けた目であなたの監視システムを設計します。監視するために何を選択する際には、次のガイドラインに従ってください

** The rules that catch real incidents most often should be as simple, predictable, and reliable as possible.

ほとんどの場合、実際の出来事をキャッチするルールは、可能な限り、シンプルな予測可能、かつ信頼できるものでなければなりません

** Data collection, aggregation, and alerting configuration that is rarely exercised (e.g., less than once a quarter for some SRE teams) should be up for removal.

めったに実施されていないデータの収集、集約、およびアラート構成
（例えば、いくつかのSREチームのために四半期に一度以下）は除去のために優先すべきです。

** Signals that are collected, but not exposed in any prebaked dashboard nor used by any alert, are candidates for removal.

収集したがprebakedダッシュボートに露出していない、
任意の警告で使用されていないシグナルは除去の候補です。

In Google’s experience, basic collection and aggregation of metrics, paired with alerting and dashboards, has worked well as a relatively standalone system. 

Googleの経験では、アラートおよびダッシュボードとペアにメトリックの基本的な収集と集計は、比較的スタンドアロンシステムとしても働いています

(In fact Google’s monitoring system is broken up into several binaries, but typically people learn about all aspects of these binaries.) 

実際には、Googleの監視システムは、いくつかのバイナリに分割されている　が、一般的に人々
これらのバイナリのすべての側面について学ぶ

It can be tempting to combine monitoring with other aspects of inspecting complex systems, such as detailed system profiling, single-process debugging, tracking details about exceptions or crashes, load testing, log collection and analysis, or traffic inspection. 

複雑なシステムの検査の他の側面での監視を組み合わせて誘惑することができます

このような詳細なシステムプロファイリング、シングルプロセスのデバッグ、例外またはクラッシュについての
詳細なtracking、テストのロード、ログの収集・分析、トラフィックの検査

While most of these subjects share commonalities with basic monitoring, blending together too many results in overly complex and fragile systems. 

これらの被験者のほとんどは基本的な監視と共通点を共有しながら、
過度に複雑で壊れやすいシステムの多くの結果を一緒に混在する

As in many other aspects of software engineering, maintaining distinct systems with clear, simple, loosely coupled points of integration is a better strategy 

ソフトウェア工学の他の多くの側面と同様に、統合の明確な、単純な、疎結合の点で明確なシステムを維持することは、より良い戦略であります

(for example, using web APIs for pulling summary data in a format that can remain constant over an extended period of time).

例えば、任意のフォーマットで要約をpullするweb apiを使用すること
長期間にわたって一定に維持することができます

* Tying These Principles Together

ともにこれらの原則を型付けすること

The principles discussed in this chapter can be tied together into a philosophy on monitoring and alerting that’s widely endorsed and followed within Google SRE teams.

この章で説明する原則は、監視とアラートの哲学に一緒に結ぶことができる
それが広くGoogleのSREチーム内で承認し、続いている

 While this monitoring philosophy is a bit aspirational, it’s a good starting point for writing or reviewing a new alert, and it can help your organization ask the right questions, regardless of the size of your organization or the complexity of your service or system.

この監視の哲学は少し野心的、
それは、書き込みや新しいアラートを見直すための良い出発点
それはあなたの組織は、組織の規模やサービスやシステムの複雑さにかかわらず、
正しい質問を尋ねることを助けることができる

When creating rules for monitoring and alerting, asking the following questions can help you avoid false positives and pager burnout:3

監視とアラートのルールを作成するとき
以下の質問をするとfalse positivesとpager burnoutを避けるのを助けることができる

** Does this rule detect an otherwise undetected condition that is urgent, actionable, and actively or imminently user-visible?

このルールは以下の場合は検出されない状態ですか？
緊急
現在有効？
またはuser-visibleに差し迫っている

** Will I ever be able to ignore this alert, knowing it’s benign? When and why will I be able to ignore this alert, and how can I avoid this scenario?

この警告は無視することができ、良性と知っている
いつ、そしてなぜこの警告を無視することができ、どのように私はこのシナリオを回避することができますか？

** Does this alert definitely indicate that users are being negatively affected? Are there detectable cases in which users aren’t being negatively impacted, such as drained traffic or test deployments, that should be filtered out?

この警告は間違いなく、ユーザーがマイナスの影響を受けていることを示していますか？

検知できるケース
・ユーザに影響があるかどうか
・排出されるトラフィック
フィルタアウトされるべきか？

** Can I take action in response to this alert? Is that action urgent, or could it wait until morning? Could the action be safely automated? Will that action be a long-term fix, or just a short-term workaround?

私は、この警告に応答して行動を取ることはできますか？
そのアクションが急務となっている、またはそれは朝まで待つことができますか？
アクションは安全に自動化することができますか？
そのアクションは、長期的な修正、または単に短期的な回避策になりますか？

** Are other people getting paged for this issue, therefore rendering at least one of the pages unnecessary?

他の人々はこのissueのために呼び出しを得る
したがって、不要な呼び出しのうち少なくとも一つを表示する？

These questions reflect a fundamental philosophy on pages and pagers:

これらの質問は、呼び出し(page)やポケベルに基本的な考え方を反映します：

** Every time the pager goes off, I should be able to react with a sense of urgency. I can only react with a sense of urgency a few times a day before I become fatigued.

pagerがオフになるたびに、私は切迫感と反応することができる必要があります。
私は疲労になる前に、私は切迫感で数回一日を反応させることができます。

** Every page should be actionable.

すべての呼び出しは実用的であるべき

** Every page response should require intelligence. If a page merely merits a robotic response, it shouldn’t be a page.

各呼び出しの応答は知性を要求（＝人が行う知的作業）するべき
もし呼び出しが単にロボット的な応答をするのであれば、それは呼び出しであるべきでない

** Pages should be about a novel problem or an event that hasn’t been seen before.

呼び出しは、新規の問題か前に見られていないイベントであるべき

Such a perspective dissipates certain distinctions: if a page satisfies the preceding four bullets, it’s irrelevant whether the page is triggered by white-box or black-box monitoring. This perspective also amplifies certain distinctions: it’s better to spend much more effort on catching symptoms than causes; when it comes to causes, only worry about very definite, very imminent causes.

このような視点は、特定の判定を散らすdissipates：

呼び出しが前の4つを満たしていれば、それは呼び出しがホワイトボックスまたはブラックボックスの監視によってトリガされるかどうかは無関係です。

この見通しは、特定の判断を広めるamplifies：

それは原因よりも症状を引くにはるかに労力を費やす方が良いでしょう。
それが原因となると、非常に明確な、非常に切迫した原因だけを心配する

* Monitoring for the Long Term

長期的な監視

In modern production systems, monitoring systems track an ever-evolving system with changing software architecture, load characteristics, and performance targets. An alert that’s currently exceptionally rare and hard to automate might become frequent, perhaps even meriting a hacked-together script to resolve it. At this point, someone should find and eliminate the root causes of the problem; if such resolution isn’t possible, the alert response deserves to be fully automated.

近代的な生産システムでは、監視システムは、
変化するソフトウェア・アーキテクチャー
負荷特性
パフォーマンスターゲット
とともに、進化し続けるシステムを追跡します。

非常に稀で、自動化が難しいアラートは頻繁になるかもしれない
おそらく　それを解決するためにa hacked-together script に値する
この時点で、誰かが見つけると、問題の根本原因を排除する必要があります。
そのような解決ができない場合、アラート応答は、完全に自動化されるに値します。

It’s important that decisions about monitoring be made with long-term goals in mind. Every page that happens today distracts a human from improving the system for tomorrow, so there is often a case for taking a short-term hit to availability or performance in order to improve the long-term outlook for the system. Let’s take a look at two case studies that illustrate this trade-off.

これは、監視に関する決定を念頭に置いて長期的な目標となることが重要です。
今日起こるすべての通知が明日のシステムを改善するから人間をそらす
システムの長期的な見通しを改善するために、可用性やパフォーマンスへの短期的な成功を取るケースがしばしばあります。
このトレードオフを示している2つのケーススタディを見てみましょう。


* Bigtable SRE: A Tale of Over-Alerting
Bigtable SRE　多すぎるアラートの逸話

Google’s internal infrastructure is typically offered and measured against a service level objective (SLO; see Chapter 4). Many years ago, the Bigtable service’s SLO was based on a synthetic well-behaved client’s mean performance. Because of problems in Bigtable and lower layers of the storage stack, the mean performance was driven by a “large” tail: the worst 5% of requests were often significantly slower than the rest.

Googleの内部インフラストラクチャは、一般的に提供され、サービスレベルの目標が考慮された（SLO）
何年も前に、BigtableのサービスのSLOは、総合的に行儀の良いのクライアントの平均パフォーマンスに基づいていました。
Bigtableやストレージ・スタックの下位層の問題がよって、平均性能は“large” tail ???? で駆動していた
リクエストのワースト5％は、多くの場合、他の部分よりも大幅に遅くなりました。

Email alerts were triggered as the SLO approached, and paging alerts were triggered when the SLO was exceeded. Both types of alerts were firing voluminously, consuming unacceptable amounts of engineering time: the team spent significant amounts of time triaging the alerts to find the few that were really actionable, and we often missed the problems that actually affected users, because so few of them did. Many of the pages were non-urgent, due to well-understood problems in the infrastructure, and had either rote responses or received no response.

SLOが近づくと電子メールアラートがトリガされ、SLOを超えたときにpagingアラートがトリガされました。
アラートの両方のタイプは、エンジニアリング時間の許容できない量を消費し、多量に発射しました。
チームは　わずかな実用的な事象を見つけるためのアラートの分類にかなりの時間を費やした
そして私たちはしばしば、実際にユーザーに影響を与えた問題を逃しました

多くの通知が緊急ではなかった
インフラではよくわからない問題だった
rote responses　または　NOレスポンスの受領のどちらかだった

To remedy the situation, the team used a three-pronged approach: while making great efforts to improve the performance of Bigtable, we also temporarily dialed back our SLO target, using the 75th percentile request latency. We also disabled email alerts, as there were so many that spending time diagnosing them was infeasible.

状況を改善するために、チームは3方面からのアプローチを使用しました。
Bigtableのパフォーマンスを向上させるために大きな努力をしながら、
我々はまた、一時的に75パーセンタイルのlatencyを使用して、私たちのSLO目標をdialed backされました。

私たちはemail alertsを無効化した
多くの時間を費やし、アラートを診断することが不可能だったので

This strategy gave us enough breathing room to actually fix the longer-term problems in Bigtable and the lower layers of the storage stack, rather than constantly fixing tactical problems. On-call engineers could actually accomplish work when they weren’t being kept up by pages at all hours. Ultimately, temporarily backing off on our alerts allowed us to make faster progress toward a better service.

この戦略は、私たちに実際にBigtableの中で長期的な問題やストレージスタックの下位層を固定するのではなく、常に戦術的な問題を解決するのに十分な余裕を与えました。

彼らはすべての時間帯でのページから追いついていなかったが、
この改善によりオンコールエンジニアが実際に作業を行うことができます。
最終的には、一時的に私たちの警告にバックオフすると、私たちはより良いサービスに向けてより速く進歩を遂げることができました。

* Gmail: Predictable, Scriptable Responses from Humans
人間から予測可能、スクリプト可能なレスポンス

In the very early days of Gmail, the service was built on a retrofitted distributed process management system called Workqueue, which was originally created for batch processing of pieces of the search index. Workqueue was “adapted” to long-lived processes and subsequently applied to Gmail, but certain bugs in the relatively opaque codebase in the scheduler proved hard to beat.

Gmailの非常に初期の頃、Workqueueと呼ばれるプロセス管理システムに構築された
それはもともと、検索インデックスの一部のバッチ処理のために作成されました。
Workqueueは、long-livedのプロセスに「改造」され
その後、Gmailに適用され、
しかし、比較的不透明なコードベースで
スケジューラと連動が難しいバグが確証された


At that time, the Gmail monitoring was structured such that alerts fired when individual tasks were “de-scheduled” by Workqueue. This setup was less than ideal because even at that time, Gmail had many, many thousands of tasks, each task representing a fraction of a percent of our users. We cared deeply about providing a good user experience for Gmail users, but such an alerting setup was unmaintainable.

その時点で、Gmailの監視は
Workqueueによって個々のタスクが「de-scheduled」になったとき
アラートを発火するよう、構築された

この設定は理想よりも少なかった
なぜならその時にGamailはたくさん、何千のタスク、我々のユーザの1%のさらに端数のタスク
を持っていた
私たちは、Gmailユーザーのための優れたユーザーエクスペリエンスの提供を深く世話したが
そのようなalertingセットアップは保守できなかった

To address this problem, Gmail SRE built a tool that helped “poke” the scheduler in just the right way to minimize impact to users. The team had several discussions about whether or not we should simply automate the entire loop from detecting the problem to nudging the rescheduler, until a better long-term solution was achieved, but some worried this kind of workaround would delay a real fix.

この問題に対処するには、GmailのSREは、ユーザーへの影響を最小限にするために
適切な方法で“poke” the schedulerを助けるツールを構築しました。

チームはいくつかの議論をしました
私たちは問題の検出からreschedulerを実行するまでの全ループをシンプルに自動化すべきかどうか
しかし、これらの回避策は、実際の修正を遅らせる心配しました。

This kind of tension is common within a team, and often reflects an underlying mistrust of the team’s self-discipline: while some team members want to implement a “hack” to allow time for a proper fix, others worry that a hack will be forgotten or that the proper fix will be deprioritized indefinitely. This concern is credible, as it’s easy to build layers of unmaintainable technical debt by patching over problems instead of making real fixes. Managers and technical leaders play a key role in implementing true, long-term fixes by supporting and prioritizing potentially time-consuming long-term fixes even when the initial “pain” of paging subsides.

この種の緊張のは、チーム内で一般的であり、多くの場合、チームの自己規律の根底にある不信感を反映しています：

いくつかのチームメンバーが適切な修正のための "ハック"を許可する時間を実装する一方で、
他の人がハックを忘れされることや、適切な修正が無期限に優先されないことを心配します。

この懸念が信用できる
保守不可能な技術的負債の層を構築するのが簡単なように
問題点を超えるパッチ適用の代わりに実際の修正を行うこともできます。

マネージャーやテクニカルリーダーは真の実現に重要な役割を果たしています

通知の初期の「痛み」が収まった時
潜在的に優先順位を付けることであっても、長期的な修正を時間がかかります

Pages with rote, algorithmic responses should be a red flag. Unwillingness on the part of your team to automate such pages implies that the team lacks confidence that they can clean up their technical debt. This is a major problem worth escalating.

機械的もしくは、アルゴリズムがある通知は、red flagであるべき
そのような通知を自動化するあなたのチームのほうの不本意は、
チームは、技術の借金をきれいにすることができる自信を欠くのを暗示している。
これは、エスカレートする価値がある主要な問題である。？？？？

* The Long Run

A common theme connects the previous examples of Bigtable and Gmail: 
共通のテーマはBigtableとGmailの前の例を接続する

a tension between short-term and long-term availability. 
短期および長期の可用性の間の緊張。

Often, sheer force of effort can help a rickety system achieve high availability, but this path is usually short-lived and fraught with burnout and dependence on a small number of heroic team members. 

多くの場合、膨大な努力はガタガタのシステムの高可用性を成し遂げることができる
しかし、このpathは通常短命で、燃え尽きる危険をはらんでいて、少数の英雄的メンバーに依存している

Taking a controlled, short-term decrease in availability is often a painful, but strategic trade for the long-run stability of the system. 

コントロールすることで、可用性の短期間の減少は
多くの場合、システムの長期安定性のために痛みを伴うが、戦略的なトレードです。

It’s important not to think of every page as an event in isolation, but to consider whether the overall level of paging leads toward a healthy, appropriately available system with a healthy, viable team and long-term outlook. We review statistics about page frequency 

それは隔離されたイベントとしてすべての通知を考えないようにすることが重要です、
しかし、通知の全体的なレベルが健康に向かってつながるかどうかを検討するために、
健康的で適切に利用可能なシステム
実行可能なチームと長期見通し。
私たちは、通知の頻度に関する統計情報を確認します

(usually expressed as incidents per shift, where an incident might be composed of a few related pages) in quarterly reports with management, ensuring that decision makers are kept up to date on the pager load and overall health of their teams.

(通常、1シフトあたりのインシデントとして表現される　インシデントはいくつか関係する通知で構成されるかもしれない)
その意思決定者を確保する経営陣と四半期報告書は、彼らのチームのページロードおよび全体的な健康に最新の状態に保たれています　？？


* Conclusion

結論

A healthy monitoring and alerting pipeline is simple and easy to reason about. 

健康的な監視およびアラートのパイプラインはシンプルで、それについての理由は簡単です。

It focuses primarily on symptoms for paging, reserving cause-oriented heuristics to serve as aids to debugging problems. 

これは、主に通知の兆しに焦点を当て、
デバッグする問題のためのAIDSとして、原因指向の体験学習を確保する？？

Monitoring symptoms is easier the further “up” your stack you monitor, though monitoring saturation and performance of subsystems such as databases often must be performed directly on the subsystem itself. 

徴候を監視することはより容易である より一層の「up」 あなたが監視するあなたのスタック 
だけれども
データベースなどのサブシステムの監視飽和と性能は、サブシステム自身においてしばしば直接実行されなければならない

Email alerts are of very limited value and tend to easily become overrun with noise; instead, you should favor a dashboard that monitors all ongoing subcritical problems for the sort of information that typically ends up in email alerts. A dashboard might also be paired with a log, in order to analyze historical correlations.

Eメール警告は、非常に制限された価値をもち、容易に、ノイズがはびこる傾向がある；
代わりに、

あなたはダッシュボードを賛成するべき
それは情報のソートのためのすべての進行中の臨界前の問題を監視する
それはEメール警告に終わらせる

ダッシュボードはまた歴史の相互関係を分析するために、ログとともに一対で構成されるべき

Over the long haul, achieving a successful on-call rotation and product includes choosing to alert on symptoms or imminent real problems, adapting your targets to goals that are actually achievable, and making sure that your monitoring supports rapid diagnosis.

長い目で見れば、
on-call rotationの成功を実現
徴候または差し迫っている本当の問題において警報を出すことを選ぶことを含む製品
実際達成可能なゴールのために、あなたの目標を適応させる
あなたの監視の急速診断をサポートすることを確かめさせる。

-----

Chapter 7. The Evolution of Automation at Google

Googleの自動化の進化

Written by Niall Murphy with John Looney and Michael Kacirek

Edited by Betsy Beyer

Besides black art, there is only automation and mechanization.

Federico García Lorca (1898–1936), Spanish poet and playwright

For SRE, automation is a force multiplier, not a panacea. 
SRE にとって、自動化は力を増強させる食べ物であるが万能薬ではない

Of course, just multiplying force does not naturally change the accuracy of where that force is applied: 

もちろん力を増強させる食べ物は、自然にその力が作用した場合の精度を変更しない

doing automation thoughtlessly can create as many problems as it solves. 

自動化することは、なまじ、多くの問題として作成し、解決することができる。

Therefore, while we believe that software-based automation is superior to manual operation in most circumstances, better than either option is a higher-level system design requiring neither of them—an autonomous system. 

したがって、我々はそのソフトウェア・ベースの自動化は、ほとんどの状況では、手動操作よりも優れていると考えている一方で、
それら(自律システム)のどちらを必要とする、より高いレベルのシステム設計は、それがいずれかのオプションよりも優れています

Or to put it another way, the value of automation comes from both what it does and its judicious application. We’ll discuss both the value of automation and how our attitude has evolved over time.

あるいは別の言い方をするために、自動化の価値は、それが何をするかとその賢明なアプリケーションの両方から来ています。
私たちは、自動化の価値と　どのように私たちの姿勢を時間をかけて進化してきたのか　の両方を説明します。

* The Value of Automation
自動化の価値

What exactly is the value of automation?1
正確に自動化の価値は何？

** Consistency
一貫性

Although scale is an obvious motivation for automation, there are many other reasons to use it. 
スケールは自動化のための明白な動機ですが、それを使用する他の多くの理由があります

Take the example of university computing systems, where many systems engineering folks started their careers. 
多くのシステムエンジニアリングの人々が自分のキャリアを始めた大学のコンピューティング・システムの例を見てみましょう

Systems administrators of that background were generally charged with running a collection of machines or some software, and were accustomed to manually performing various actions in the discharge of that duty. 

その背景のシステム管理者は、一般的にマシンの集合またはいくつかのソフトウェアの実行することを引き受け
そして、手動でさまざまなアクションを実行するのに慣れました

One common example is creating user accounts; 

1つの一般的な例は、ユーザーアカウントの作成

others include purely operational duties like making sure backups happen, managing server failover, and small data manipulations like changing the upstream DNS servers’ resolv.conf, DNS server zone data, and similar activities. 
・たしかなバックアップファイルを作成するような運用業務
・サーバーのフェイルオーバーの管理
・上流のDNSサーバーのresolv.confを変更するような小さなデータ操作、
・DNSサーバーのゾーンデータ
・そして似たような活動

Ultimately, however, this prevalence of manual tasks is unsatisfactory for both the organizations and indeed the people maintaining systems in this way. 

最終的に、しかしながら手動タスクの普及は、組織、およびシステムを維持する人々両方に満足できるものではありません

For a start, any action performed by a human or humans hundreds of times won’t be performed the same way each time: 

スタートのために、人々によって実行されたアクションは、同じ方法を毎回実行されません。

even with the best will in the world, very few of us will ever be as consistent as a machine. 

でも、世界で最高の意志と、私たちの非常に少数は、これまでのマシンのように一貫性があります。

This inevitable lack of consistency leads to mistakes, oversights, issues with data quality, and, yes, reliability problems. 

一貫性のこの避けられないことは、データ品質、信頼性の問題でミス、見落とし、問題につながります。

In this domain—the execution of well-scoped, known procedures—the value of consistency is in many ways the primary value of automation.

よくスコープ、知られているこのドメイン実行の手順、一貫性の値は、自動化の主要な価値は、多くの方法です。

** A Platform

プラットフォーム

Automation doesn’t just provide consistency. 
自動化は、単に一貫性を提供していません。

Designed and done properly, automatic systems also provide a platform that can be extended, applied to more systems, or perhaps even spun out for profit.
(The alternative, no automation, is neither cost effective nor extensible: it is instead a tax levied on the operation of a system.)

設計され、正しく行われる
自動システムは、また、拡張可能なプラットフォームを提供します
より多くのシステムに適用される、またはおそらく利益のために長く保たれる

（代替、自動化でない　どちらのコストが効果的でも拡張可能です。
　それは代わりに、システムの動作に課される税金です）

A platform also centralizes mistakes. 
プラットフォームは、間違いを一元化します。

In other words, a bug fixed in the code will be fixed there once and forever, unlike a sufficiently large set of humans performing the same procedure, as discussed previously. 

つまり、コードでのバグFixは、一度の修正で、永遠に修正されます
前述したように、同じ手順を実行する人間の十分に大きなセットとは異なり。

A platform can be extended to perform additional tasks more easily than humans can be instructed to perform them (or sometimes even realize that they have to be done). 

プラットフォームは、それらを実行するために、より容易に人間が指示することができるよりも、
追加のタスクを実行するように拡張することができる（または時には彼らが行わなければならないことを実現）

Depending on the nature of the task, it can run either continuously or much more frequently than humans could appropriately accomplish the task, or at times that are inconvenient for humans. 

タスクの性質に応じて、それは人間が適切にタスクを達成できたよりも頻繁に連続的に又は多くの実行
時として人間には不便

Furthermore, a platform can export metrics about its performance, or otherwise allow you to discover details about your process you didn’t know previously, because these details are more easily measurable within the context of a platform.

これらの詳細は、より簡単に、プラットフォームのコンテキスト内で測定可能であるため、
また、プラットフォームは、あなたが以前に知らなかったあなたのプロセスについての詳細を発見することができそうでなければ、
その性能についてのmetricsをエクスポートすることができます

** Faster Repairs

より早く修理

There’s an additional benefit for systems where automation is used to resolve common faults in a system 
自動化は、システムに共通の障害を解決するために使用されるシステムのための付加的な利点があります

(a frequent situation for SRE-created automation). 

SREが作成した自動化の頻繁な状況

If automation runs regularly and successfully enough, the result is a reduced mean time to repair (MTTR) for those common faults. 

自動化が定期的に、正常に十分に実行された場合、結果はMTTR（平均復旧時間）を減らせること

You can then spend your time on other tasks instead, thereby achieving increased developer velocity because you don’t have to spend time either preventing a problem or (more commonly) cleaning up after it. 

あなたはそれの後にクリーンアップ（より一般的に）時間のいずれかの問題を防止またはを費やす必要はありませんので、
あなたは、それによって増加した開発者の速度を達成し、代わりに他のタスクにあなたの時間を過ごすことができます。

As is well understood in the industry, the later in the product lifecycle a problem is discovered, the more expensive it is to fix; 

当業界で理解されているように、製品ライフサイクルの問題の後で発見された場合、それを修正するのはよく高価です

ee Chapter 17. 

Generally, problems that occur in actual production are most expensive to fix, both in terms of time and money, which means that an automated system looking for problems as soon as they arise has a good chance of lowering the total cost of the system, given that the system is sufficiently large.

一般的に、実際の生産で発生する問題は、修正するのが最も高価であり、
時間とお金の両方で、
自動化されたシステムは、システムの総コストを下げるのに良いチャンスがあります
システムが十分に大きいことを考えます。

** Faster Action

早いアクション

In the infrastructural situations where SRE automation tends to be deployed, humans don’t usually react as fast as machines. 
SREの自動化が展開される傾向にあるインフラの状況では、人間は通常、マシンほど速く反応しません

In most common cases, where, for example, failover or traffic switching can be well defined for a particular application, it makes no sense to effectively require a human to intermittently press a button called “Allow system to continue to run.” 

最も一般的な例では、
例えば、フェイルオーバーまたはトラフィック切り替えが特定のアプリケーションのためによく定義される

それが
効果的に人間を必要とし
“Allow system to continue to run.” と呼ばれるボタンを断続的にボタンを押すのは
意味がありません

(Yes, it is true that sometimes automatic procedures can end up making a bad situation worse, but that is why such procedures should be scoped over well-defined domains.) 
(はい、時々自動手続きが悪化悪い状況を作ってしまうことは事実です
このような手順は、明確に定義された領域を渡ってスコープされるべき理由がある)

Google has a large amount of automation; in many cases, the services we support could not long survive without this automation because they crossed the threshold of manageable manual operation long ago.
Googlaは、自動化の量が多いです。多くの場合、
彼らはずっと前に管理可能な手動操作のしきい値を超えているため、我々がサポートするサービスは
長い間、この自動化せずに生き残ることができませんでした。

** Time Saving

時間の節約

Finally, time saving is an oft-quoted rationale for automation. 
最後に、時間の節約には、自動化のためのよく引き合いに出される根拠があります。

Although people cite this rationale for automation more than the others, in many ways the benefit is often less immediately calculable. 
人々はより多くの他のものよりも自動化のため、この理論的根拠を引用しているが、
多くの点で利点は、あまりすぐに計算可能です。

Engineers often waver over whether a particular piece of automation or code is worth writing, in terms of effort saved in not requiring a task to be performed manually versus the effort required to write it.

エンジニアはしばしば、自動化やコードの特定の部分が価値がある書き込みかどうかを迷う
それを書くために必要な努力に対して手動で行うことができるため
タスクが必要でないと思う？

 It’s easy to overlook the fact that once you have encapsulated some task in automation, anyone can execute the task. 
それはあなたが自動でいくつかのタスクをカプセル化した後、誰がタスクを実行することができるという事実を見落とすことは簡単です

 Therefore, the time savings apply across anyone who would plausibly use the automation. 
そのため、時間の節約がもっともらしく、オートメーションを使用することになり、誰もがに適用されます

 Decoupling operator from operation is very powerful.
操作からオペレータを切り離すことは非常に強力です。

----
** WARNING

Joseph Bironas, an SRE who led Google’s datacenter turnup efforts for a time, forcefully argued:
Joseph Bironas Googleのデータセンターを率いるSREは時間のために努力をやってのける　強制的に主張します

“If we are engineering processes and solutions that are not automatable, we continue having to staff humans to maintain the system. 
私たちは、エンジニアリング・プロセスと自動化されない解決策がある場合は、
我々は、システムを維持するために人間をスタッフに持ち続けます。

If we have to staff humans to do the work, we are feeding the machines with the blood, sweat, and tears of human beings.
私たちが仕事をする人間をスタッフに持っている場合は、
私たちは、人間の血、汗、そして涙を用いて、マシンを運用しています。

Think The Matrix with less special effects and more pissed off System Administrators.”

特別な効果が少ないマトリックス？行列？を考えて、より多くのシステム管理者が去りました
-----

* The Value for Google SRE
GoogleのSREのための価値

All of these benefits and trade-offs apply to us just as much as anyone else, and Google does have a strong bias toward automation. 

これらの利点とトレードオフのすべてが、同じくらい他の誰として私たちに適用されます
そして、Googleは、自動化に向けた強いバイアスを持っています。

Part of our preference for automation springs from our particular business challenges: 

当社の特定のビジネス上の課題から自動化？のための私達の優先する部分：

the products and services we look after are planet-spanning in scale, and we don’t typically have time to engage in the same kind of machine or service hand-holding common in other organizations.4 

私たちの製品とサービスは、地球にまたがる規模であり
私たちは一般的に従事する時間がありません
・マシンの同じ種類で
・サービスの手作業での保持

For truly large services, the factors of consistency, quickness, and reliability dominate most conversations about the trade-offs of performing automation.
本当に大規模なサービスの場合、
自動化を行う、行わないのトレードオフについてのほとんどの会話が
一貫性、迅速性、および信頼性の要因に支配されている。

Another argument in favor of automation, particularly in the case of Google, is our complicated yet surprisingly uniform production environment, described in Chapter 2.

自動化を支持する別のargument、特にグーグルの場合には、
私達の複雑さ、また驚くほど均一な生産環境である　Chapter 2で述べる

 While other organizations might have an important piece of equipment without a readily accessible API, software for which no source code is available, or another impediment to complete control over production operations, Google generally avoids such scenarios. 

他の組織は、容易にアクセス可能なAPIなしに装置の重要な部分を持っているかもしれませんが、
・ソースコードが使用できないソフトウェア
・または生産活動の制御を完了するための別の障害
グーグルは、一般的にそのようなシナリオを回避します

 We have built APIs for systems when no API was available from the vendor. 
ベンダーから利用可能なAPIを入手できなかったとき、私たちは、システム用のAPIを構築しています

 Even though purchasing software for a particular task would have been much cheaper in the short term, we chose to write our own solutions, because doing so produced APIs with the potential for much greater long-term benefits. 

短期的かつはるかに安価な　　特定のタスクのためのソフトウェアを購入するよりも
我々は我々自身のソリューションを書くことを選びました、はるかに大きな長期的な利益が潜在的するAPIにするため

 We spent a lot of time overcoming obstacles to automatic system management, and then resolutely developed that automatic system management itself. 

我々は、自動システム管理の障害を克服するのに多くの時間を費やし、その後、あえてその自動システム管理自体を開発しました。

 Given how Google manages its source code [Pot16], the availability of that code for more or less any system that SRE touches also means that our mission to “own the product in production” is much easier because we control the entirety of the stack.

 Googleは、そのソースコード[Pot16]はどのように管理するかを考えると、

だいたいまた、SREが触れるどんなシステムのためのそのコードの入手可能性でも、我々がスタックの全部を支配するので、
"own the product in production" ??? 我々の任務が非常により簡単なことを意味します。

Of course, although Google is ideologically bent upon using machines to manage machines where possible, reality requires some modification of our approach. 
当然のことながら、可能な所でGoogleが機械を管理するために機械を使うことに観念的に心を傾けているが、
現実は我々のアプローチのいくらかの修正を必要とします。

It isn’t appropriate to automate every component of every system, and not everyone has the ability or inclination to develop automation at a particular time. 

すべてのシステムのすべてのコンポーネントを自動化することは適切ではありません
そして、誰もが、特定時間に自動化を開発する能力や傾きを持っています。

Some essential systems started out as quick prototypes, not designed to last or to interface with automation. The previous paragraphs state a maximalist view of our position, but one that we have been broadly successful at putting into action within the Google context. 

いくつかの重要なシステムでは、迅速なプロトタイプとしてスタートし
最終的にもしくは自動化のインタフェースとして設計されていませんでした

前項は、我々の最大限要求主義者の見解を述べましたが
我々は、Googleコンテキスト内で行動に入れることに広く成功していました

In general, we have chosen to create platforms where we could, or to position ourselves so that we could create platforms over time. 

一般的に、私たちは以下のプラットフォームを作成することを選択してきました
　私達が可能、もしくは自身を配置すること
　我々が時間をかけてのプラットフォームを作成することができるように。

We view this platform-based approach as necessary for manageability and scalability.
私たちは、管理性と拡張性のために、必要に応じてこのplatform-basedのアプローチを表示します

* The Use Cases for Automation

自動化のためのユースケース

In the industry, automation is the term generally used for writing code to solve a wide variety of problems, although the motivations for writing this code, and the solutions themselves, are often quite different. 
業界では、自動化は、一般的に、問題の様々を解決するコードを書くために使用される用語であり、
だけれども、このコードを書くための動機、およびソリューション自体が、しばしば非常に異なっています。

More broadly, in this view, automation is “meta-software”—software to act on software.

より広く、このviewでは、自動化は、ソフトウェアに基づいて行動する"meta-software"です

As we implied earlier, there are a number of use cases for automation. Here is a non-exhaustive list of examples:

我々は以前に示唆されるように、自動化のためのユースケースがいくつかあります。ここでの例の非網羅的なリストであります

* User account creation
ユーザアカウントの作成

* Cluster turnup and turndown for services
クラスタのturnup/turndown

* Software or hardware installation preparation and decommissioning
ソフトウェアまたはハードウェアのインストール準備と廃止

* Rollouts of new software versions
新しいソフトウェアバージョンのロールアウト

* Runtime configuration changes
ランタイム構成の変更

* A special case of runtime config changes: changes to your dependencies
ランタイム構成変更の特殊なケース：あなたの依存関係の変更

This list could continue essentially ad infinitum.
このリストは、本質的に無限に続けることができました。

** Google SRE’s Use Cases for Automation
自動化のためのGoogle SREのユースケース

In Google, we have all of the use cases just listed, and more.
グーグルでは、私たちは記載されている使用例を多く持っています。

However, within Google SRE, our primary affinity has typically been for running infrastructure, as opposed to managing the quality of the data that passes over that infrastructure. 

しかし、Google SRE内で、
私たちの主な親和性は、典型的には、インフラストラクチャを実行するためにされ
そのインフラストラクチャ上を通過するデータの品質を管理することとは対照的です。

This line isn’t totally clear—for example, we care deeply if half of a dataset vanishes after a push, and therefore we alert on coarse-grain differences like this, but it’s rare for us to write the equivalent of changing the properties of some arbitrary subset of accounts on a system. 

この行は全く明確ではありません
例えば
データセットの半分がプッシュした後に消滅した場合、我々は深く心配する
そのため私たちは、このような粗粒度の違い？に警告します
しかし、システム上のアカウントのいくつかの任意のサブセットの特性を変化させると同等のものを書くのは稀です　？？？


Therefore, the context for our automation is often automation to manage the lifecycle of systems, not their data: for example, deployments of a service in a new cluster.
したがって、私たちの自動化のためのコンテキストはシステムのライフサイクルを管理するために、多くの場合、自動化され、
それらのデータではない？

例えば新クラスタ上のサービスの配備

To this extent, SRE’s automation efforts are not far off what many other people and organizations do, except that we use different tools to manage it and have a different focus (as we’ll discuss).

この範囲まで
SREの自動化の取り組みは、これまで他の多くの人々や組織が何をするか、はるかかなたではありません
それを管理し、異なる焦点を持つために、異なるツールを使用することを除いて
（as we’ll discuss）

Widely available tools like Puppet, Chef, cfengine, and even Perl, which all provide ways to automate particular tasks, differ mostly in terms of the level of abstraction of the components provided to help the act of automating. 

Puppet, Chef, cfengine, Perlのような広く利用可能なツール
そのすべてが、特定のタスクを自動化する方法を提供します
自動化の行為を助けるために提供されるコンポーネントの抽象化のレベルに関して、主に異なります。

A full language like Perl provides POSIX-level affordances, which in theory provide an essentially unlimited scope of automation across the APIs accessible to the system,5 whereas Chef and Puppet provide out-of-the-box abstractions with which services or other higher-level entities can be manipulated. The trade-off here is classic: 
Perlのような完全な言語は、POSIXレベルのアフォーダンス（環境は内包している力）？を提供し、
システムにアクセスするAPI全体で自動化の本質的に無制限の範囲を提供する理論的上は
ChefやPuppetは、サービスまたは他の上位レベルのエンティティを操作することが可能な、枠を超える抽象化を提供するに反して
ここでのトレードオフは古典的です

higher-level abstractions are easier to manage and reason about, but when you encounter a “leaky abstraction,” you fail systemically, repeatedly, and potentially inconsistently. 

より高いレベルの抽象化は、管理し、それについての理由は容易です
しかし、あなたが“leaky abstraction,”（漏れやすい抽象化？？？）遭遇したときに
あなたは損なう
・全身的に
・繰り返して
・潜在的に
・無節操に

For example, we often assume that pushing a new binary to a cluster is atomic; the cluster will either end up with the old version, or the new version. 

たとえば
私たちはしばしばクラスタに新しいバイナリをプッシュすることはatomicであることを前提としています。
 atomic - すべて完了するか、しないか　原子性
 クラスタは、いずれかの古いバージョン、または新しいバージョンになる

However, real-world behavior is more complicated: 

しかし、実世界の挙動はより複雑です。

that cluster’s network can fail halfway through; 
そのクラスタのネットワークは途中で失敗する可能性があります。

machines can fail; 
マシンが失敗する場合があります。

communication to the cluster management layer can fail, leaving the system in an inconsistent state; 
クラスタ管理層への通信は、失敗する可能性があり、一貫性のない状態でシステムを残ります。

depending on the situation, new binaries could be staged but not pushed, or pushed but not restarted, or restarted but not verifiable. 
状況に応じて、新しいバイナリがstageされるがpushされていない、あるいはプッシュされるが再起動されない
再起動するが検証可能ではない

Very few abstractions model these kinds of outcomes successfully, and most generally end up halting themselves and calling for intervention. 
非常に少数の抽象化は、成功した結果のこれらの種類をモデル化します
そして、最も一般的に自身を停滞させ、介入を求めます。

Truly bad automation systems don’t even do that.
真に悪い自動化システムはそうすることでさせしない

SRE has a number of philosophies and products in the domain of automation, some of which look more like generic rollout tools without particularly detailed modeling of higher-level entities, and some of which look more like languages for describing service deployment (and so on) at a very abstract level. 

SREは、自動化のドメインでいくつかの哲学や製品をもっている
そのうちのいくつかは、より高いレベルのエンティティの特に詳細なモデリングなしに、
より一般的なロールアウトツールのように見えます
そしてそのうちのいくつかは、非常に抽象的なレベルで（など）サービスの展開を記述するための言語のように、よりに見えます。

Work done in the latter tends to be more reusable and be more of a common platform than the former, but the complexity of our production environment sometimes means that the former approach is the most immediately tractable option.

後者で行われた作業をより再利用可能で、元よりも共通のプラットフォームをより多くなる傾向があり、
しかし、当社の生産環境の複雑さは、時には前者のアプローチは、ほとんどすぐに扱いやすい選択肢であることを意味しています。

** A Hierarchy of Automation Classes

自動化のクラスの階層

Although all of these automation steps are valuable, and indeed an automation platform is valuable in and of itself, in an ideal world, we wouldn’t need externalized automation. 

これらの自動化手順のすべてが貴重であるが、
実際オートメーションプラットフォームは、それ自体の価値があります
理想的な世界では、我々は客観化する自動化を必要としないであろう。

In fact, instead of having a system that has to have external glue logic, it would be even better to have a system that needs no glue logic at all, not just because internalization is more efficient (although such efficiency is useful), but because it has been designed to not need glue logic in the first place. 

実際には、代わりに外部のglue logic？？を有していなければならないシステムを有します
　glue logic = 複数の集積回路を相互に接続する際に、外付けする論理回路

それは全くglue logicを必要としないシステムを持っていることも良いだろう、
内在化はより効率的であるため
（このような効率は便利ですが）
しかし、それは最初にglue logicを必要としないように設計されているため。

Accomplishing that involves taking the use cases for glue logic—generally “first order” manipulations of a system, such as adding accounts or performing system turnup—and finding a way to handle those use cases directly within the application.

それを遂行することは、glue logicのためのユースケースを取ることを伴っている

一般に、システムの「一次」の操作、？？
このようなアカウントの追加やシステムの折り返し（turnup）を行うなど
およびアプリケーション内で直接それらのユースケースを処理するための方法を見つけます。

As a more detailed example, most turnup automation at Google is problematic because it ends up being maintained separately from the core system and therefore suffers from “bit rot,” i.e., not changing when the underlying systems change. 

より詳細な例として、Googleの最もturnupな自動化は問題があります
なぜならそれは、中心的なシステムから別々に維持されることを最終的にし、「bit rot」に苦しむ
すなわち、基盤となるシステムが変更されたときに変化していません。

Despite the best of intentions, attempting to more tightly couple the two (turnup automation and the core system) often fails due to unaligned priorities, as product developers will, not unreasonably, resist a test deployment requirement for every change. 

最善の意図にもかかわらず、
その2つをより堅く結合することを試みる
（turnup automationとコアシステム）

しばしば　非整列の優先順位に従い失敗する
製品開発者として　不合理ではなく
すべての変更のためのテスト展開の要件に反対する

Secondly, automation that is crucial but only executed at infrequent intervals and therefore difficult to test is often particularly fragile because of the extended feedback cycle. 

第２に、
重大であるが、たまにしか実行されない、テストが難しい自動化は特に壊れやすい
拡張されたフィードバックサイクルのため、

Cluster failover is one classic example of infrequently executed automation: failovers might only occur every few months, or infrequently enough that inconsistencies between instances are introduced. 

クラスタフェールオーバーが頻繁に実行されない自動化の一典型的な例です：

フェイルオーバーは、数ヶ月ごとに発生する可能性があります
または、まれに十分なインスタンス間で不整合を引き合わせる

The evolution of automation follows a path:

自動化の進化は以下？の経路に従います。

1) No automation

自動化しない

Database master is failed over manually between locations.

データベースのマスターは、ロケーション間で手動でフェイルオーバーされます。

2) Externally maintained system-specific automation

外部で管理されている特別なシステムの自動化

An SRE has a failover script in his or her home directory.

SREは彼/彼女のホームディレクトリにフェイルオーバースクリプトを保有している

3) Externally maintained generic automation

外部で管理されている汎用的な自動化

The SRE adds database support to a “generic failover” script that everyone uses.

SREは誰もが使用する「汎用的なフェールオーバー」スクリプトにデータベースのサポートを追加します。

4) Internally maintained system-specific automation

内部で管理されている特別なシステムの自動化

The database ships with its own failover script.

データベースは自身のフェールオーバースクリプトを保有している

5) Systems that don’t need any automation

任意の自動化を必要としないシステム

The database notices problems, and automatically fails over without human intervention.

データベースが問題に気づき、そして自動的に人間の介入なしにフェイルオーバーします。

SRE hates manual operations, so we obviously try to create systems that don’t require them. 

SREは、手動操作が嫌い、私たちは、明らかにそれらを必要としないシステムを作成しようとする

However, sometimes manual operations are unavoidable.

しかし、時々手動操作が避けられません。

There is additionally a subvariety of automation that applies changes not across the domain of specific system-related configuration, but across the domain of production as a whole. 

自動化にはさらに亜種がある
　特定の関係するシステムの設定のドメインに向かってではなく
　全体としてproductionのドメインに向かって
変更を適用する

In a highly centralized proprietary production environment like Google’s, there are a large number of changes that have a non–service-specific scope—e.g.,

Googleのような高度に中央集権独自の本番環境では、
非サービス固有のスコープを持っている多数の変更があります　例えば。。

changing upstream Chubby servers, a flag change to the Bigtable client library to make access more reliable, and so on—which nonetheless need to be safely managed and rolled back if necessary. 

upstream Chubby servers　の変更
アクセスの信頼性を高めるために、Bigtableのクライアントライブラリへのフラグの変更　など
これはそれにもかかわらず、安全に管理し、必要に応じてロールバックする必要があります。

Beyond a certain volume of changes, it is infeasible for production-wide changes to be accomplished manually, and at some time before that point, it’s a waste to have manual oversight for a process where a large proportion of the changes are either trivial or accomplished successfully by basic relaunch-and-check strategies.

変更の一定量を超えて、
生産規模の変更は手動で達成することは、不可能で
その時点の前にいくつかの時点で、

変更の大部分は、いずれかの些細なことや、
基本的なrelaunch-and-checkの戦略によって正常に達成されているプロセスのための
手動の監視を持つことは、無駄です。

Let’s use internal case studies to illustrate some of the preceding points in detail. 

それでは、詳細に先行するいくつかのポイントを説明するために、内部ケーススタディを使用してみましょう。

The first case study is about how, due to some diligent, far-sighted work, 

最初のケーススタディでは、いくつかの勤勉、far-sighted?? workにどう従うか

we managed to achieve the self-professed nirvana of SRE: to automate ourselves out of a job.

私たちは、SREの自称：安息の境地を達成するために管理しました：ジョブの中から、私達自身を自動化します。

* Automate Yourself Out of a Job: Automate ALL the Things!

ジョブの中からあなた自身を自動化する：すべてのものを自動化します

For a long while, the Ads products at Google stored their data in a MySQL database. 

長い間、Googleの広告製品は、MySQLデータベース内のデータを格納されています

Because Ads data obviously has high reliability requirements, an SRE team was charged with looking after that infrastructure. 

広告データは、明らかに、高い信頼性要件を有しているので、SREチームはそのインフラストラクチャの世話を投入しました

From 2005 to 2008, the Ads Database mostly ran in what we considered to be a mature and managed state. 

2005年から2008年まで、広告データベースは、成熟で状態が管理されていると考えて実行されていました

For example, we had automated away the worst, but not all, of the routine work for standard replica replacements. 

たとえば、私たちは最悪を考えないで？自動化した
　すべてではないが、標準的なレプリカの交換のためのルーチンワーク

We believed the Ads Database was well managed and that we had harvested most of the low-hanging fruit in terms of optimization and scale. 

私たちは、広告データベースは、よく管理されたと信じた
最適化と規模の点から、手に取りやすいフルーツだけを採取する？？？

However, as daily operations became comfortable, team members began to look at the next level of system development:

しかしながら、日常業務は、快適になりました
チームのメンバーは、システム開発の次のレベルを見始めました。

migrating MySQL onto Google’s cluster scheduling system, Borg.
Googleのクラスタ・スケジューリング・システム(Borg)にMySQLを移行します

We hoped this migration would provide two main benefits:

私たちは、この移行は、2つの主な利点を提供することを望みました

* Completely eliminate machine/replica maintenance: Borg would automatically handle the setup/restart of new and broken tasks.

完全にマシン/レプリカのメンテナンスを排除：
Borgは自動的にnew とbroken tasksのsetup/restartを処理します。

* Enable bin-packing of multiple MySQL instances on the same physical machine: Borg would enable more efficient use of machine resources via Containers.

同じ物理マシン上で複数のMySQLインスタンスのbin-packing??(荷をつめる箱の最小数を求める)を有効にします。
Borgは、コンテナを介してマシンのリソースのより効率的な使用を可能にします。

In late 2008, we successfully deployed a proof of concept MySQL instance on Borg. 

2008年後半では、Borg上で、MySQL instanceの概念実証の配備に成功しました

Unfortunately, this was accompanied by a significant new difficulty. 

残念ながら、これは重要な新しい困難を伴っていました。

A core operating characteristic of Borg is that its tasks move around automatically. 

Borgの主となる動作特性は、そのタスクが自動的に動き回ることです。

Tasks commonly move within Borg as frequently as once or twice per week. 

タスクは、週1回、または2回の頻度で、Borg内を移動します。

This frequency was tolerable for our database replicas, but unacceptable for our masters.

この頻度は、当社のデータベースレプリカのために許容しました
しかし、あなたのmasters？？？に受け入れられません。

At that time, the process for master failover took 30–90 minutes per instance. 

その時、マスター・フェイルオーバーのためのプロセスは、インスタンスごとに30-90分を要しました。

Simply because we ran on shared machines and were subject to reboots for kernel upgrades, in addition to the normal rate of machine failure, we had to expect a number of otherwise unrelated failovers every week. 

私たちは共有マシン上で実行し、カーネルのアップグレードのために再起動する対象となったという理由だけで、
機械の故障の通常速度に加えて、毎週関係のないフェイルオーバーの数を予期しなければならなかった

This factor, in combination with the number of shards on which our system was hosted, meant that:

この要因は、当社のシステムがホストされたshardの数との組み合わせで、次のことを意味しました

* Manual failovers would consume a substantial amount of human hours and would give us best-case availability of 99% uptime, which fell short of the actual business requirements of the product.

手動フェイルオーバーは、人間の時間のかなりの量を消費するし、私達に99% uptimeの最良の可用性を与えるだろう
これは、製品の実際のビジネス要件を上回りました（fell short）

* In order to meet our error budgets, each failover would have to take less than 30 seconds of downtime. There was no way to optimize a human-dependent procedure to make downtime shorter than 30 seconds.

私たちの誤差予算を満たすために、各フェールオーバーは、ダウンタイムの30秒未満を取る必要があります。
ダウンタイムより短い30秒を作るために人間に依存する手順を最適化する方法はありませんでした

Therefore, our only choice was to automate failover. Actually, we needed to automate more than just failover.

したがって、私たちの唯一の選択肢は、フェールオーバーを自動化することでした。
実際は、私たちはフェイルオーバーよりも多くを自動化する必要があった

In 2009 Ads SRE completed our automated failover daemon, which we dubbed “Decider.” 

2009年に広告SREは、当社の自動フェイルオーバーデーモンを完了しました
私たちは「Decider」と称した

Decider could complete MySQL failovers for both planned and unplanned failovers in less than 30 seconds 95% of the time. 

Deciderは、95%が30秒未満の時間で
計画的および計画外両方のフェールオーバーのためのMySQLのフェイルオーバーを完了することができます。

With the creation of Decider, MySQL on Borg (MoB) finally became a reality. 

Deciderの作成をもって、MySQL on Borg (MoB)はついに現実のものとなりました。

We graduated from optimizing our infrastructure for a lack of failover to embracing the idea that failure is inevitable, and therefore optimizing to recover quickly through automation.

私たちは、障害が不可避であるという考えを受け入れるため、
自動化により迅速に回復するため、
フェイルオーバーの欠如のためのインフラストラクチャ最適化を卒業しました

While automation let us achieve highly available MySQL in a world that forced up to two restarts per week, it did come with its own set of costs. 
自動化は、私たちは週に2回再起動までアップし  世界で可用性の高いMySQLを実現させているが、それはコストのそれ自身のセットと来た？（コストが独自にかかっている？）

All of our applications had to be changed to include significantly more failure-handling logic than before.
我々のアプリケーションのすべては、以前よりもかなり多くの障害処理ロジックを含むように変更しなければなりませんでした


Given that the norm in the MySQL development world is to assume that the MySQL instance will be the most stable component in the stack, this switch meant customizing software like JDBC to be more tolerant of our failure-prone environment. 

MySQLの開発の世界では当たり前のこと、MySQLインスタンスがスタックの中で最も安定した成分であると仮定すると、

このスイッチは、故障が発生しやすい環境をよりtolerantとする為
JDBCのようなソフトウェアをカスタマイズすることを意味します

However, the benefits of migrating to MoB with Decider were well worth these costs. 

しかしながら、DeciderでMOBへの移行の利点は、価値があるコストでした

Once on MoB, the time our team spent on mundane operational tasks dropped by 95%. 
かつてMOB上で、私たちのチームは、日常的な運用タスクに費やす時間を95％減少しました。

Our failovers were automated, so an outage of a single database task no longer paged a human.
私たちのフェイルオーバーは、自動化されました、単一のデータベースタスクの停止で人への通知はもはやありません。

The main upshot of this new automation was that we had a lot more free time to spend on improving other parts of the infrastructure. 
この新しい自動化の主な結論は、我々は多くをインフラストラクチャの他の部分を改善することに費やすために、多くの自由な時間を持っていたことでした。

Such improvements had a cascading effect: the more time we saved, the more time we were able to spend on optimizing and automating other tedious work. 

このような改善は、相乗効果？がありました：
より多くの時間を我々が確保することができ、より多くの時間を我々は、最適化と自動化、他の面倒な作業に費やすことができました。

Eventually, we were able to automate schema changes, causing the cost of total operational maintenance of the Ads Database to drop by nearly 95%. 

最終的に
我々は、Adsデータベースの総運用保守のコストの原因であった
スキーマの変更を自動化することができました（コストをほど95%低下した？）

Some might say that we had successfully automated ourselves out of this job. 

いくつかの人は、この仕事以外の自分自身を自動化に成功した。と言うかもしれません。

The hardware side of our domain also saw improvement. 

私たちのドメインのハードウェア側にも改善が見られました。

Migrating to MoB freed up considerable resources because we could schedule multiple MySQL instances on the same machines, which improved utilization of our hardware. 

私たちは、同じマシン上で複数のMySQLインスタンスをスケジュールすることができ、私たちのハードウェアの使用率を改善したので
MoBにMigratingすることは、かなりのresourcesを解放しました

In total, we were able to free up about 60% of our hardware. 

合計で、私たちは、ハードウェアの約60％を解放することができました。

Our team was now flush with hardware and engineering resources.

我々のチームは、その時ハードウェアとエンジニアリングresourcesをあり余るほど持っていました。

This example demonstrates the wisdom of going the extra mile to deliver a platform rather than replacing existing manual procedures. 

この例は、既存の手動処置を取り替えることよりもむしろプラットホームを届けるために一層の努力をすることの知恵を示します。

The next example comes from the cluster infrastructure group, and illustrates some of the more difficult trade-offs you might encounter on your way to automating all the things.

次の例では、cluster infrastructure group　から来ています
そして、より困難なトレードオフのいくつかを示しています
あなたはすべてのものを自動化するために、あなたの方法で遭遇する可能性があります。

ここで半分くらい
* Soothing the Pain: Applying Automation to Cluster Turnups

Ten years ago, the Cluster Infrastructure SRE team seemed to get a new hire every few months. As it turned out, that was approximately the same frequency at which we turned up a new cluster. Because turning up a service in a new cluster gives new hires exposure to a service’s internals, this task seemed like a natural and useful training tool.

The steps taken to get a cluster ready for use were something like the following:

Fit out a datacenter building for power and cooling.

Install and configure core switches and connections to the backbone.

Install a few initial racks of servers.

Configure basic services such as DNS and installers, then configure a lock service, storage, and computing.

Deploy the remaining racks of machines.

Assign user-facing services resources, so their teams can set up the services.

Steps 4 and 6 were extremely complex. While basic services like DNS are relatively simple, the storage and compute subsystems at that time were still in heavy development, so new flags, components, and optimizations were added weekly.

Some services had more than a hundred different component subsystems, each with a complex web of dependencies. Failing to configure one subsystem, or configuring a system or component differently than other deployments, is a customer-impacting outage waiting to happen.

In one case, a multi-petabyte Bigtable cluster was configured to not use the first (logging) disk on 12-disk systems, for latency reasons. A year later, some automation assumed that if a machine’s first disk wasn’t being used, that machine didn’t have any storage configured; therefore, it was safe to wipe the machine and set it up from scratch. All of the Bigtable data was wiped, instantly. Thankfully we had multiple real-time replicas of the dataset, but such surprises are unwelcome. Automation needs to be careful about relying on implicit “safety” signals.

Early automation focused on accelerating cluster delivery. This approach tended to rely upon creative use of SSH for tedious package distribution and service initialization problems. This strategy was an initial win, but those free-form scripts became a cholesterol of technical debt.

** Detecting Inconsistencies with Prodtest

As the numbers of clusters grew, some clusters required hand-tuned flags and settings. As a result, teams wasted more and more time chasing down difficult-to-spot misconfigurations. If a flag that made GFS more responsive to log processing leaked into the default templates, cells with many files could run out of memory under load. Infuriating and time-consuming misconfigurations crept in with nearly every large configuration change.

The creative—though brittle—shell scripts we used to configure clusters were neither scaling to the number of people who wanted to make changes nor to the sheer number of cluster permutations that needed to be built. These shell scripts also failed to resolve more significant concerns before declaring that a service was good to take customer-facing traffic, such as:

Were all of the service’s dependencies available and correctly configured?

Were all configurations and packages consistent with other deployments?

Could the team confirm that every configuration exception was desired?

Prodtest (Production Test) was an ingenious solution to these unwelcome surprises. We extended the Python unit test framework to allow for unit testing of real-world services. These unit tests have dependencies, allowing a chain of tests, and a failure in one test would quickly abort. Take the test shown in Figure 7-1 as an example.

ProdTest for DNS Service, showing how one failed test aborts the subsequent chain of tests.
Figure 7-1. ProdTest for DNS Service, showing how one failed test aborts the subsequent chain of tests
A given team’s Prodtest was given the cluster name, and it could validate that team’s services in that cluster. Later additions allowed us to generate a graph of the unit tests and their states. This functionality allowed an engineer to see quickly if their service was correctly configured in all clusters, and if not, why. The graph highlighted the failed step, and the failing Python unit test output a more verbose error message.

Any time a team encountered a delay due to another team’s unexpected misconfiguration, a bug could be filed to extend their Prodtest. This ensured that a similar problem would be discovered earlier in the future. SREs were proud to be able to assure their customers that all services—both newly turned up services and existing services with new configuration—would reliably serve production traffic.

For the first time, our project managers could predict when a cluster could “go live,” and had a complete understanding of why each clusters took six or more weeks to go from “network-ready” to “serving live traffic.” Out of the blue, SRE received a mission from senior management: In three months, five new clusters will reach network-ready on the same day. Please turn them up in one week.

** Resolving Inconsistencies Idempotently

A “One Week Turnup” was a terrifying mission. We had tens of thousands of lines of shell script owned by dozens of teams. We could quickly tell how unprepared any given cluster was, but fixing it meant that the dozens of teams would have to file hundreds of bugs, and then we had to hope that these bugs would be promptly fixed.

We realized that evolving from “Python unit tests finding misconfigurations” to “Python code fixing misconfigurations” could enable us to fix these issues faster.

The unit test already knew which cluster we were examining and the specific test that was failing, so we paired each test with a fix. If each fix was written to be idempotent, and could assume that all dependencies were met, resolving the problem should have been easy—and safe—to resolve. Requiring idempotent fixes meant teams could run their “fix script” every 15 minutes without fearing damage to the cluster’s configuration. If the DNS team’s test was blocked on the Machine Database team’s configuration of a new cluster, as soon as the cluster appeared in the database, the DNS team’s tests and fixes would start working.

Take the test shown in Figure 7-2 as an example. If TestDnsMonitoringConfigExists fails, as shown, we can call FixDnsMonitoringCreateConfig, which scrapes configuration from a database, then checks a skeleton configuration file into our revision control system. Then TestDnsMonitoringConfigExists passes on retry, and the TestDnsMonitoringConfigPushed test can be attempted. If the test fails, the FixDnsMonitoringPushConfig step runs. If a fix fails multiple times, the automation assumes that the fix failed and stops, notifying the user.

Armed with these scripts, a small group of engineers could ensure that we could go from “The network works, and machines are listed in the database” to “Serving 1% of websearch and ads traffic” in a matter of a week or two. At the time, this seemed to be the apex of automation technology.

Looking back, this approach was deeply flawed; the latency between the test, the fix, and then a second test introduced flaky tests that sometimes worked and sometimes failed. Not all fixes were naturally idempotent, so a flaky test that was followed by a fix might render the system in an inconsistent state.

ProdTest for DNS Service, showing that one failed test resulted in only running one fix.
Figure 7-2. ProdTest for DNS Service, showing that one failed test resulted in only running one fix

** The Inclination to Specialize
Automation processes can vary in three respects:

Competence, i.e., their accuracy

Latency, how quickly all steps are executed when initiated

Relevance, or proportion of real-world process covered by automation

We began with a process that was highly competent (maintained and run by the service owners), high-latency (the service owners performed the process in their spare time or assigned it to new engineers), and very relevant (the service owners knew when the real world changed, and could fix the automation).

To reduce turnup latency, many service owning teams instructed a single “turnup team” what automation to run. The turnup team used tickets to start each stage in the turnup so that we could track the remaining tasks, and who those tasks were assigned to. If the human interactions regarding automation modules occurred between people in the same room, cluster turnups could happen in a much shorter time. Finally, we had our competent, accurate, and timely automation process!

But this state didn’t last long. The real world is chaotic: software, configuration, data, etc. changed, resulting in over a thousand separate changes a day to affected systems. The people most affected by automation bugs were no longer domain experts, so the automation became less relevant (meaning that new steps were missed) and less competent (new flags might have caused automation to fail). However, it took a while for this drop in quality to impact velocity.

Automation code, like unit test code, dies when the maintaining team isn’t obsessive about keeping the code in sync with the codebase it covers. The world changes around the code: the DNS team adds new configuration options, the storage team changes their package names, and the networking team needs to support new devices.

By relieving teams who ran services of the responsibility to maintain and run their automation code, we created ugly organizational incentives:

A team whose primary task is to speed up the current turnup has no incentive to reduce the technical debt of the service-owning team running the service in production later.

A team not running automation has no incentive to build systems that are easy to automate.

A product manager whose schedule is not affected by low-quality automation will always prioritize new features over simplicity and automation.

The most functional tools are usually written by those who use them. A similar argument applies to why product development teams benefit from keeping at least some operational awareness of their systems in production.

Turnups were again high-latency, inaccurate, and incompetent—the worst of all worlds. However, an unrelated security mandate allowed us out of this trap. Much of distributed automation relied at that time on SSH. This is clumsy from a security perspective, because people must have root on many machines to run most commands. A growing awareness of advanced, persistent security threats drove us to reduce the privileges SREs enjoyed to the absolute minimum they needed to do their jobs. We had to replace our use of sshd with an authenticated, ACL-driven, RPC-based Local Admin Daemon, also known as Admin Servers, which had permissions to perform those local changes. As a result, no one could install or modify a server without an audit trail. Changes to the Local Admin Daemon and the Package Repo were gated on code reviews, making it very difficult for someone to exceed their authority; giving someone the access to install packages would not let them view colocated logs. The Admin Server logged the RPC requestor, any parameters, and the results of all RPCs to enhance debugging and security audits.

** Service-Oriented Cluster-Turnup

In the next iteration, Admin Servers became part of service teams’ workflows, both as related to the machine-specific Admin Servers (for installing packages and rebooting) and cluster-level Admin Servers (for actions like draining or turning up a service). SREs moved from writing shell scripts in their home directories to building peer-reviewed RPC servers with fine-grained ACLs.

Later on, after the realization that turnup processes had to be owned by the teams that owned the services fully sank in, we saw this as a way to approach cluster turnup as a Service-Oriented Architecture (SOA) problem: service owners would be responsible for creating an Admin Server to handle cluster turnup/turndown RPCs, sent by the system that knew when clusters were ready. In turn, each team would provide the contract (API) that the turnup automation needed, while still being free to change the underlying implementation. As a cluster reached “network-ready,” automation sent an RPC to each Admin Server that played a part in turning up the cluster.

We now have a low-latency, competent, and accurate process; most importantly, this process has stayed strong as the rate of change, the number of teams, and the number of services seem to double each year.

As mentioned earlier, our evolution of turnup automation followed a path:

Operator-triggered manual action (no automation)

Operator-written, system-specific automation

Externally maintained generic automation

Internally maintained, system-specific automation

Autonomous systems that need no human intervention

While this evolution has, broadly speaking, been a success, the Borg case study illustrates another way we have come to think of the problem of automation.

* Borg: Birth of the Warehouse-Scale Computer

Another way to understand the development of our attitude toward automation, and when and where that automation is best deployed, is to consider the history of the development of our cluster management systems.6 Like MySQL on Borg, which demonstrated the success of converting manual operations to automatic ones, and the cluster turnup process, which demonstrated the downside of not thinking carefully enough about where and how automation was implemented, developing cluster management also ended up demonstrating another lesson about how automation should be done. Like our previous two examples, something quite sophisticated was created as the eventual result of continuous evolution from simpler beginnings.

Google’s clusters were initially deployed much like everyone else’s small networks of the time: racks of machines with specific purposes and heterogeneous configurations. Engineers would log in to some well-known “master” machine to perform administrative tasks; “golden” binaries and configuration lived on these masters. As we had only one colo provider, most naming logic implicitly assumed that location. As production grew, and we began to use multiple clusters, different domains (cluster names) entered the picture. It became necessary to have a file describing what each machine did, which grouped machines under some loose naming strategy. This descriptor file, in combination with the equivalent of a parallel SSH, allowed us to reboot (for example) all the search machines in one go. Around this time, it was common to get tickets like “search is done with machine x1, crawl can have the machine now.”

Automation development began. Initially automation consisted of simple Python scripts for operations such as the following:

Service management: keeping services running (e.g., restarts after segfaults)

Tracking what services were supposed to run on which machines

Log message parsing: SSHing into each machine and looking for regexps

Automation eventually mutated into a proper database that tracked machine state, and also incorporated more sophisticated monitoring tools. With the union set of the automation available, we could now automatically manage much of the lifecycle of machines: noticing when machines were broken, removing the services, sending them to repair, and restoring the configuration when they came back from repair.

But to take a step back, this automation was useful yet profoundly limited, due to the fact that abstractions of the system were relentlessly tied to physical machines. We needed a new approach, hence Borg [Ver15] was born: a system that moved away from the relatively static host/port/job assignments of the previous world, toward treating a collection of machines as a managed sea of resources. Central to its success—and its conception—was the notion of turning cluster management into an entity for which API calls could be issued, to some central coordinator. This liberated extra dimensions of efficiency, flexibility, and reliability: unlike the previous model of machine “ownership,” Borg could allow machines to schedule, for example, batch and user-facing tasks on the same machine.

This functionality ultimately resulted in continuous and automatic operating system upgrades with a very small amount of constant7 effort—effort that does not scale with the total size of production deployments. Slight deviations in machine state are now automatically fixed; brokenness and lifecycle management are essentially no-ops for SRE at this point. Thousands of machines are born, die, and go into repairs daily with no SRE effort. To echo the words of Ben Treynor Sloss: by taking the approach that this was a software problem, the initial automation bought us enough time to turn cluster management into something autonomous, as opposed to automated. We achieved this goal by bringing ideas related to data distribution, APIs, hub-and-spoke architectures, and classic distributed system software development to bear upon the domain of infrastructure management.

An interesting analogy is possible here: we can make a direct mapping between the single machine case and the development of cluster management abstractions. In this view, rescheduling on another machine looks a lot like a process moving from one CPU to another: of course, those compute resources happen to be at the other end of a network link, but to what extent does that actually matter? Thinking in these terms, rescheduling looks like an intrinsic feature of the system rather than something one would “automate”—humans couldn’t react fast enough anyway. Similarly in the case of cluster turnup: in this metaphor, cluster turnup is simply additional schedulable capacity, a bit like adding disk or RAM to a single computer. However, a single-node computer is not, in general, expected to continue operating when a large number of components fail. The global computer is—it must be self-repairing to operate once it grows past a certain size, due to the essentially statistically guaranteed large number of failures taking place every second. This implies that as we move systems up the hierarchy from manually triggered, to automatically triggered, to autonomous, some capacity for self-introspection is necessary to survive.

* Reliability Is the Fundamental Feature

Of course, for effective troubleshooting, the details of internal operation that the introspection relies upon should also be exposed to the humans managing the overall system. Analogous discussions about the impact of automation in the noncomputer domain—for example, in airplane flight8 or industrial applications—often point out the downside of highly effective automation:9 human operators are progressively more relieved of useful direct contact with the system as the automation covers more and more daily activities over time. Inevitably, then, a situation arises in which the automation fails, and the humans are now unable to successfully operate the system. The fluidity of their reactions has been lost due to lack of practice, and their mental models of what the system should be doing no longer reflect the reality of what it is doing.10 This situation arises more when the system is nonautonomous—i.e., where automation replaces manual actions, and the manual actions are presumed to be always performable and available just as they were before. Sadly, over time, this ultimately becomes false: those manual actions are not always performable because the functionality to permit them no longer exists.

We, too, have experienced situations where automation has been actively harmful on a number of occasions—see “Automation: Enabling Failure at Scale”—but in Google’s experience, there are more systems for which automation or autonomous behavior are no longer optional extras. As you scale, this is of course the case, but there are still strong arguments for more autonomous behavior of systems irrespective of size. Reliability is the fundamental feature, and autonomous, resilient behavior is one useful way to get that.

* Recommendations

You might read the examples in this chapter and decide that you need to be Google-scale before you have anything to do with automation whatsoever. This is untrue, for two reasons: automation provides more than just time saving, so it’s worth implementing in more cases than a simple time-expended versus time-saved calculation might suggest. But the approach with the highest leverage actually occurs in the design phase: shipping and iterating rapidly might allow you to implement functionality faster, yet rarely makes for a resilient system. Autonomous operation is difficult to convincingly retrofit to sufficiently large systems, but standard good practices in software engineering will help considerably: having decoupled subsystems, intro

--
** AUTOMATION: ENABLING FAILURE AT SCALE
Google runs over a dozen of its own large datacenters, but we also depend on machines in many third-party colocation facilities (or “colos”). Our machines in these colos are used to terminate most incoming connections, or as a cache for our own Content Delivery Network, in order to lower end-user latency. At any point in time, a number of these racks are being installed or decommissioned; both of these processes are largely automated. One step during decommission involves overwriting the full content of the disk of all the machines in the rack, after which point an independent system verifies the successful erase. We call this process “Diskerase.”

Once upon a time, the automation in charge of decommissioning a particular rack failed, but only after the Diskerase step had completed successfully. Later, the decommission process was restarted from the beginning, to debug the failure. On that iteration, when trying to send the set of machines in the rack to Diskerase, the automation determined that the set of machines that still needed to be Diskerased was (correctly) empty. Unfortunately, the empty set was used as a special value, interpreted to mean “everything.” This means the automation sent almost all the machines we have in all colos to Diskerase.

Within minutes, the highly efficient Diskerase wiped the disks on all machines in our CDN, and the machines were no longer able to terminate connections from users (or do anything else useful). We were still able to serve all the users from our own datacenters, and after a few minutes the only effect visible externally was a slight increase in latency. As far as we could tell, very few users noticed the problem at all, thanks to good capacity planning (at least we got that right!). Meanwhile, we spent the better part of two days reinstalling the machines in the affected colo racks; then we spent the following weeks auditing and adding more sanity checks—including rate limiting—into our automation, and making our decommission workflow idempotent.
--
