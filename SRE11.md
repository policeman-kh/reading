## Feeling Safe

安心すること

As mentioned earlier, SRE teams support Google’s most critical systems.

前述したように、SREチームは、Googleの最も重要なシステムをサポートしています。

Being an SRE on-call typically means assuming responsibility for user-facing, revenue-critical systems or for the infrastructure required to keep these systems up and running.

SREのon-callであることは、典型的に、
ユーザ向けで収益に不可欠なシステムのため、あるいはこれらのシステムの起動と稼働の維持が必須であるインフラのため、責任を負うことを意味します。

SRE methodology for thinking about and tackling problems is vital for the appropriate operation of services.

それらについて考え、問題に取り組むためのSREの方法論は
サービスの適切な運用のために不可欠です。

Modern research identifies two distinct ways of thinking that an individual may, consciously or subconsciously, choose when faced with challenges [Kah11]:

最新のリサーチは、課題を抱えた時に、個人的、意識的、潜在的に選択する考え方の
2つの方法を証明している。

[Kah11]
https://google-engtools.blogspot.jp/2011/08/build-in-cloud-how-build-system-works.html

* Intuitive, automatic, and rapid action

直感的で、オートマティックで、迅速なアクション

* Rational, focused, and deliberate cognitive functions

合理的に、集中して、意図的に認識機能

When one is dealing with the outages related to complex systems, the second of these options is more likely to produce better results and lead to well-planned incident handling.

？one = 何を意味している？

ある一人が複合システムに関係する障害を扱っている時
これらのオプションである二人目は、より良い結果を生成し、良く計画されたインシデントハンドリングにつながる可能性が高いです。

incident=出来事・事件

To make sure that the engineers are in the appropriate frame of mind to leverage the latter mindset, it’s important to reduce the stress related to being on-call.

エンジニアが後者の考え方を導入して、気持ちを適切にすることを確かめるには
on-callであることに関係したストレスを軽減することが重要です。

The importance and the impact of the services and the consequences of potential outages can create significant pressure on the on-call engineers, damaging the well-being of individual team members and possibly prompting SREs to make incorrect choices that can endanger the availability of the service.

重大さとサービスの影響と潜在的な障害の結果は、on-callエンジニアに大きなプレッシャーを与え、サービスの可用性を危険にさらす誤った選択を行うため、個々のチームメンバー、ひょっとして迅速なSREsの健康にダメージを与えます。

Stress hormones like cortisol and corticotropin-releasing hormone (CRH) are known to cause behavioral consequences—including fear—that can impair cognitive functions and cause suboptimal decision making [Chr09].

コルチゾールとコルチコトロピン-releasingホルモン（CRH）のようなストレスホルモンは
認識機能（cognitive functions）を損ない、最適ではない意思決定（suboptimal decision making）を引き起こす。と知られている

Under the influence of these stress hormones, the more deliberate cognitive approach is typically subsumed by unreflective and unconsidered (but immediate) action, leading to potential abuse of heuristics.

これらのストレスホルモンの影響下で、より慎重な認知的アプローチは典型的に
思慮深くない（unreflective）、
不用意な（unconsidered）、（しかし即時な）アクションによって、
包摂され、潜在的な経験則の乱用を導く

Heuristics are very tempting behaviors when one is on-call.

on-callであるとき、経験則は非常に魅力的な行動です。

For example, when the same alert pages for the fourth time in the week, and the previous three pages were initiated by an external infrastructure system, it is extremely tempting to exercise confirmation bias by automatically associating this fourth occurrence of the problem with the previous cause.

例えば、週に4回、同じアラート通知があり、前の4つの通知はい外部のインフラシステムによって引き起こされた時、前の原因と4つ目の発生を関連付けることにより、確証バイアス(confirmation bias)を行使することは非常に魅力的です。

While intuition and quick reactions can seem like desirable traits in the middle of incident management, they have downsides.

直感と迅速な反応は、インシデント管理の真っ只中で望ましい特徴のように思えるが、それらは欠点を持っています

Intuition can be wrong and is often less supportable by obvious data.

直感は間違っていることがあり、しばしば　明らかなデータによって支持されない

Thus, following intuition can lead an engineer to waste time pursuing a line of reasoning that is incorrect from the start.

したがって、以下の直感は、最初から間違っている論法を追求し時間を無駄にするエンジニアにつながる

Quick reactions are deep-rooted in habit, and habitual responses are unconsidered, which means they can be disastrous.

迅速な反応は習慣上根強くあり、習慣的なレスポンスは不用意で、すなわち壊滅的である

The ideal methodology in incident management strikes the perfect balance of taking steps at the desired pace when enough data is available to make a reasonable decision while simultaneously critically examining your assumptions.

インシデント管理の理想的な方法論は、あなたの仮定を同時に、かつ批判的に調べる一方、
理になかった決定をするために、十分なデータが入手され、望ましいペースで措置を講じる完璧なバランスをとる

It’s important that on-call SREs understand that they can rely on several resources that make the experience of being on-call less daunting than it may seem.

on-call SREsは、そう思えるよりも、on-callの経験をいかす複数のリソースを
頼りにできることを理解することが重要です

The most important on-call resources are:

最も重要なon-call リソースは次のとおりです

* Clear escalation paths

クリアなエスカレーションパス

* Well-defined incident-management procedures

明確に定義されたインシデント管理手順

* A blameless postmortem culture ([Loo10], [All12])

ブレームレスポストモータム文化
＝個人批判をしない建設的な障害の振返りミーティングの文化

The developer teams of SRE-supported systems usually participate in a 24/7 on-call rotation, and it is always possible to escalate to these partner teams when necessary.

SREにサポートされたシステムの開発者チームは、通常、24時間365日on-call ローテーションに参加し、必要なとき、これらのパートナーチームにエスカレートすることが常に可能です

The appropriate escalation of outages is generally a principled way to react to serious outages with significant unknown dimensions.

障害の適切なエスカレーションは、一般的に、
未知の規模と思われる重大な障害に反応するために、原則的な方法です。

When one is handling incidents, if the issue is complex enough to involve multiple teams or if, after some investigation, it is not yet possible to estimate an upper bound for the incident’s time span, it can be useful to adopt a formal incident-management protocol.

ある一人がインシデントを扱っている時、
問題が複数のチームを巻き込むのに十分複雑(complex)であれば、または、
いくつかの調査の後、インシデント期間の上限を評価できないのであれば、
正式なインシデント管理のプロトコルを採用することは役に立つことができる

Google SRE uses the protocol described in Chapter 14, which offers an easy-to-follow and well-defined set of steps that aid an on-call engineer to rationally pursue a satisfactory incident resolution with all the required help.

GoogleのSREは、Chapter 14に記述しているプロトコルを使用している
合理的に必要なすべての助けを借りて、満足なインシデントの解決を追求するために
on-callエンジニアを支援する、わかりやすく明確に定義されたステップのセットを
提供している

This protocol is internally supported by a web-based tool that automates most of the incident management actions, such as handing off roles and recording and communicating status updates.

このプロトコルは、内部的に、ロールのハンドオフや記録、ステータス更新の通知のような
インシデント管理のアクションのほとんどを自動化したWebベースのツールでサポートされています。

This tool allows incident managers to focus on dealing with the incident, rather than spending time and cognitive effort on mundane actions such as formatting emails or updating several communication channels at once.

このツールより、インシデント管理者はインシデントを扱うことに集中することができます
時間と認識努力を、Eメールの校正やいくつかコミュニケーションチャネルの同時更新のような
ありふれた行動に費やすことよりも

Finally, when an incident occurs, it’s important to evaluate what went wrong, recognize what went well, and take action to prevent the same errors from recurring in the future.

最後に、インシデントが発生した時、
・何が悪かったか評価する
・何が良かったか認識する
・同じエラーが将来繰り返されるのを防ぐために、アクションを起こす
ことが重要です。

SRE teams must write postmortems after significant incidents and detail a full timeline of the events that occurred.

SREチームは重大なインシデントの後、事後分析（postmortems）と起こったイベントの詳細なタイムライン全てを記述しなければならない。

By focusing on events rather than the people, these postmortems provide significant value.

人よりもむしろイベントに集中することによって、これらの事後分析は、重要な価値を提供します。

Rather than placing blame on individuals, they derive value from the systematic analysis of production incidents.

非難を個人に置くより、彼らは本番環境のインシデントの組織的な分析から価値を得ます。

Mistakes happen, and software should make sure that we make as few mistakes as possible.

間違いは起こります。そして、ソフトウェアはできるだけ我々の間違いが少なくなるよう、確認しなければなりません。

Recognizing automation opportunities is one of the best ways to prevent human errors [Loo10].

自動化の機会を認識することは人的ミス（human errors）を回避するベストな方法の１つです。

## Avoiding Inappropriate Operational Load

不適切な運用負荷の回避

As mentioned in “Balanced On-Call”, SREs spend at most 50% of their time on operational work.

「Balanced On-Call」で述べたとおり、SREsは、多くとも50%の時間を運用の時間を費やしている

What happens if operational activities exceed this limit?

運用活動がこの制限を超えた場合はどうなりますか？

### Operational Overload

運用過負荷

The SRE team and leadership are responsible for including concrete objectives in quarterly work planning in order to make sure that the workload returns to sustainable levels.

SREチームと指導者（leadership）は、作業負荷が持続可能な水準に戻ることを確かめるために、四半期ごとの作業プランで具体的な目標を含む責任がある

Temporarily loaning an experienced SRE to an overloaded team, discussed in Chapter 30, can provide enough breathing room so that the team can make headway in addressing issues.

一時的に、過負荷なチームに経験豊かなSREを配置することは（Chapter 30で記述）
チームが問題に対処する上で進捗するように、十分な呼吸室（breathing room）＝余裕　
を提供することができます

Ideally, symptoms of operational overload should be measurable, so that the goals can be quantified (e.g., number of daily tickets < 5, paging events per shift < 2).

理想的には、運用過負荷の症状は、目標を定量化することができるように、測定可能でなければならない
（例えば、デイリーのチケット数が5未満、1シフトあたりの通知のイベント数が2未満）

Misconfigured monitoring is a common cause of operational overload.

不適切に設定された監視は、運用過負荷の一般的な原因です。

Paging alerts should be aligned with the symptoms that threaten a service’s SLOs.

通知アラートは、サービスのSLOに脅威を与える兆候は整列するべきです。

All paging alerts should also be actionable.

すべての通知アラートもまた実用的にすべきです。

Low-priority alerts that bother the on-call engineer every hour (or more frequently) disrupt productivity, and the fatigue such alerts induce can also cause serious alerts to be treated with less attention than necessary.

毎時間（もしくはより頻繁に）on-callエンジニアを悩ます優先度の低いアラートは生産性を中断させる
そして、そのようなアラートが誘発する疲労は、不注意に扱われて、重大なアラートの原因になりうる

See Chapter 29 for further discussion.

さらなる議論はChapter 29を参照

It is also important to control the number of alerts that the on-call engineers receive for a single incident.

on-callエンジニアが単一のインシデントで受信するアラート数を制御することも重要です。

Sometimes a single abnormal condition can generate several alerts, so it’s important to regulate the alert fan-out by ensuring that related alerts are grouped together by the monitoring or alerting system.

時々、単一の異常な状態はいろいろなアラートが生成される。そして、監視や警告システムによって
関連するアラートが一つにグルーピングされることを確実にすることで、アラートの展開（fan-out）を規制することは重要です。

If, for any reason, duplicate or uninformative alerts are generated during an incident, silencing those alerts can provide the necessary quiet for the on-call engineer to focus on the incident itself.

いずれにせよ、二重、あるいは情報価値のないアラートがインシデント中に生成されるのであれば
これらのアラートを沈黙させることは、on-callエンジニアにインシデントそのものに集中させるために
必要な静けさを与えることができます。

Noisy alerts that systematically generate more than one alert per incident should be tweaked to approach a 1:1 alert/incident ratio.

1インシデントあたり1つ以上のアラートを体系的に生成するノイジーなアラートは
アラートとインシデントが1:1の比率に近づくよう、調整(tweake)させるべきです

Doing so allows the on-call engineer to focus on the incident instead of triaging duplicate alerts.

そうすることは、on-callエンジニアが二重のアラートのトリアージ(行動順位決定)の代わりに、インシデントに集中することを許します。

Sometimes the changes that cause operational overload are not under the control of the SRE teams.

時々、運用過負荷を引き起こす変更は、SREチームの管理下でありません。

For example, the application developers might introduce changes that cause the system to be more noisy, less reliable, or both.

たとえば、アプリケーション開発者が
よりノイジーになる、より信頼性を損なう、もしくは両方を
システムに引き起こす変更を導入するかもしれない

In this case, it is appropriate to work together with the application developers to set common goals to improve the system.

このケースでは、システムを改善するための共通のゴールを設定するために、
アプリケーション開発者と共に働くことが、適切です。

In extreme cases, SRE teams may have the option to “give back the pager”—SRE can ask the developer team to be exclusively on-call for the system until it meets the standards of the SRE team in question.

極端な場合、SREチームは"ポケベルを（開発チームに）返す" 選択肢を持っているかもしれない
SREチームの標準を満たすまで、独占的にシステムのon-callになるよう、SREは開発者チームに依頼することができる

Giving back the pager doesn’t happen very frequently, because it’s almost always possible to work with the developer team to reduce the operational load and make a given system more reliable.

運用負荷を減らして、システムをより信頼できるようにするために、開発者チームとともに働くことがたいていできるので、ポケベルを返すことは頻繁に発生しません。

In some cases, though, complex or architectural changes spanning multiple quarters might be required to make a system sustainable from an operational point of view.

いくつかのケースでは、複雑、あるいは複数の四半期（multiple quarters）にまたがるアーキテクチャの変更にもかかわらず、経営上の観点(an operational point of view)から、システムを持続させることが必須になるかもしれない

In such cases, the SRE team should not be subject to an excessive operational load.

このような場合には、SREチームは過剰な運用負荷を受けるべきではない

Instead, it is appropriate to negotiate the reorganization of on-call responsibilities with the development team, possibly routing some or all paging alerts to the developer on-call.

その代わりに、開発チームとon-callの責任の再編を取り決め、
通知アラートのいくつか、あるいは全てをdeveloper on-callにできる限りルーティングすること
は適切です。

Such a solution is typically a temporary measure, during which time the SRE and developer teams work together to get the service in shape to be on-boarded by the SRE team again.

そのようなソリューションは典型的に一時的な対応です
その間に、SREチームが再び乗り組む(on-boardedする)構成のシステムを得るため
SREと開発チームは、ともに働きます

The possibility of renegotiating on-call responsibilities between SRE and product development teams attests to the balance of powers between the teams.

SREと製品開発チームの間で、on-callの責任を再交渉する可能性は、チームの間のパワーバランスを証明します。

This working relationship also exemplifies how the healthy tension between these two teams and the values that they represent —reliability versus feature velocity— is typically resolved by greatly benefiting the service and, by extension, the company as a whole.

このworking relationshipもまた
2つのチームと、彼らを象徴する価値の好ましい緊張(healthy tension)の良い例となる

「信頼性 VS 機能のリリース速度(feature velocity)」は典型的に
大いに利益をもたらすサービスと、拡大して(by extension)会社全体によって、解決される

### A Treacherous Enemy: Operational Underload

梟敵(きょうてき)・天敵(A Treacherous Enemy): 運用における負荷不足

Being on-call for a quiet system is blissful, but what happens if the system is too quiet or when SREs are not on-call often enough?

静かなシステムのために依頼であることは至福です。しかし、システムが静かすぎる、または、SREsが十分がon-callでないとき、何が起こりますか？

An operational underload is undesirable for an SRE team.

運用における負荷不足は、SREチームのために望ましくありません。

Being out of touch with production for long periods of time can lead to confidence issues, both in terms of overconfidence and underconfidence, while knowledge gaps are discovered only when an incident occurs.

長期間、本番環境に接触しないことは、問題を信用する結果につながる
自信過剰と自信喪失(underconfidence)が兼ね備え、
インシデントが発生した時だけ知識のギャップを気づいて

To counteract this eventuality, SRE teams should be sized to allow every engineer to be on-call at least once or twice a quarter, thus ensuring that each team member is sufficiently exposed to production.

この万一の場合を打ち消すために、
SREチームは、すべてのエンジニアが4半期につき、少なくとも1、または2回、on-callにあることを確保する
このように、各々のチーム・メンバーが本番環境に十分に触れさせることを確実とします

“Wheel of Misfortune” exercises (discussed in Chapter 28) are also useful team activities that can help to hone and improve troubleshooting skills and knowledge of the service.

“Wheel of Misfortune”の実践（第28章で述べる）は、
トラブルシューティング技術とサービス知識を磨き、向上させるのを助けることができる
役に立つチーム活動でもある

Google also has a company-wide annual disaster recovery event called DiRT (Disaster Recovery Training) that combines theoretical and practical drills to perform multiday testing of infrastructure systems and individual services; see [Kri12].

Googleはまた
インフラシステムと個々のサービスのMultiday Testingを実行するために
理論的で実用的な訓練（drill）を兼ね備えた
DiRT (Disaster Recovery Training) を呼ばれる
全社的に年一回の disaster recovery event を持っている

## Conclusions

結論

The approach to on-call described in this chapter serves as a guideline for all SRE teams in Google and is key to fostering a sustainable and manageable work environment.

この章で説明したon-callのアプローチは
GoogleのすべてのSREチームのためのガイドラインとして役に立ち
持続可能で管理可能な労働環境を育成することへのキーとなります

Google’s approach to on-call has enabled us to use engineering work as the primary means to scale production responsibilities
and maintain high reliability and availability despite the increasing complexity and number of systems and services for which SREs are responsible.

on-callへのGoogleのアプローチは、
本番環境への責任をスケールするための主要な手段として
エンジニアリング作業で使用できるようになってきました。
そして、SREsが担当しているシステムやサービスの複雑さや数が増えているにもかかわらず、高い信頼性と可用性を維持しています。

While this approach might not be immediately applicable to all contexts in which engineers need to be on-call for IT services,
we believe it represents a solid model that organizations can adopt in scaling to meet a growing volume of on-call work.

このアプローチは
ITサービスのために、エンジニアがon-callになる必要があるすべてのコンテキストにすぐに適用できないかもしれないが、
組織がon-callの仕事の増加を迎えるために
スケールで採用することができるソリッドモデルを表している
と私たちは信じています
