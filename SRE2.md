# 第12章 効果的なトラブルシューティング

クリス・ジョーンズによって書かれました

Be warned that being an expert is more than understanding how a system is supposed to work.

エキスパートである事は、どのようにシステムが動作するのか理解する以上であることを警告します

Expertise is gained by investigating why a system doesn’t work.

専門知識は、システムが動作しない理由を調査することによって得られます。

Brian Redman

Ways in which things go right are special cases of the ways in which things go wrong.

物事が正しく進む方法は、誤って進む方法の特殊なケースです。　

John Allspaw

Troubleshooting is a critical skill for anyone who operates distributed computing systems
—especially SREs—

トラブルシューティングは、分散コンピュータ・システムを操作するいかなる人にとって重要なスキルです。
特に、SREsにとって

but it’s often viewed as an innate skill that some people have and others don’t.

しかし、それは、いくつかの人が保有していて、他はない先天的なスキルとしてしばしば見られます。

One reason for this assumption is that, for those who troubleshoot often, it’s an ingrained process;  

この仮定の1つの理由は、多くトラブルシューティングを行う人のための、生まれつきのプロセスです。

explaining how to troubleshoot is difficult, much like explaining how to ride a bike.

トラブルシューティングの方法を説明することは難しい。自転車の乗り方を説明するように。

However, we believe that troubleshooting is both learnable and teachable.

しかしながら、私たちは トラブルシューティングは学習可能で、教育可能である両方を信じています

Novices are often tripped up when troubleshooting because the exercise ideally depends upon two factors: an understanding of how to troubleshoot generically
(i.e., without any particular system knowledge) and a solid knowledge of the system.

トラブルシューティングを行うときに、初心者はしばしばつまずきます。実習は理想的に２つの要因に依存するので：
一般的にトラブルシューティングの方法の理解（特有のシステム知識なしに）とシステムの深い知識。


While you can investigate a problem using only the generic process and derivation from first principles, we usually find this approach to be less efficient and less effective than understanding how things are supposed to work.

あなたが　最初の原理から、一般的なプロセスと派生を使用した問題を調査可能な間、
私たちは通常、物事が動作する方法を理解するよりも　効率的でない、効果的でない、このアプローチを見つける

Knowledge of the system typically limits the effectiveness of an SRE new to a system; there’s little substitute to learning how the system is designed and built.

システムの知識は一般的に、新しいSREの有効性を一つのシステムに制限します
どのようにシステムがデザインされて、構築されるか学習するためには、ほとんどの代替があります。

Let’s look at a general model of the troubleshooting process.

トラブルシューティングプロセスの一般的なモデルを見てみましょう。

Readers with expertise in troubleshooting may quibble with our definitions and process; if your method is effective for you, there’s no reason not to stick with it.

トラブルシューティングの専門知識を持つ読者は、私たちの定義とプロセスとつまらない議論だと思うかもしれない。
もし、あなたのメソッドがあなたにとって有効であれば、それに固執しない理由はありません。

## Theory

理論

Formally, we can think of the troubleshooting process as an application of the hypothetico-deductive method:

形式的には、我々は仮説演繹法のアプリケーションとして、トラブルシューティングのプロセスを考えることができます：

仮説演繹法：（hypothetico-deductive method）
https://ja.wikipedia.org/wiki/%E4%BB%AE%E8%AA%AC%E6%BC%94%E7%B9%B9%E6%B3%95

given a set of observations about a system and a theoretical basis for understanding system behavior, we iteratively hypothesize potential causes for the failure and try to test those hypotheses.

システムと、システムの振る舞いを理解するための理論的基礎について、観察の一式が与えられ、
私達は失敗のための潜在的な原因を繰り返し仮定し、それらの仮説をテストしようとします。

In an idealized model such as that in Figure 12-1,

Figure 12-1 のような理想的なモデルの中で、

we’d start with a problem report telling us that something is wrong with the system.

私たちは、何かシステムがよくないことを示している問題のレポート報告から始めます。

Then we can look at the system’s telemetry and logs to understand its current state.

それから、私たちは現在の状態を理解するために、システムのテリトリーとログを見ることができます。

This information, combined with our knowledge of how the system is built, how it should operate, and its failure modes, enables us to identify some possible causes.

この情報は、どうシステムが構築されるか、どう操作すべきか、そのfailureモードと組み合わせされて
起こりうる原因を見極めることを可能にする


Figure 12-1. A process for troubleshooting

図12-1 トラブルシューティングのためのプロセス

We can then test our hypotheses in one of two ways.

それから、我々は2つの方法の1つで、仮説をテストすることができます。

We can compare the observed state of the system against our theories to find confirming or disconfirming evidence.

私たちは、確証、あるいは未確証の証拠を見つけるため
私たちの理論に対するシステムの監視状態を比較することができます。

Or, in some cases, we can actively “treat” the system
— that is, change the system in a controlled way—
and observe the results.

または、いくつかのケースでは、我々は積極的にシステムを「治療」することができる
-- つまり、コントロールされた方向にシステムを変更する
そしてその結果を監視する

This second approach refines our understanding of the system’s state and possible cause(s) of the reported problems.

この第2のアプローチは、システムの状態の理解と報告された問題の起こりうる原因に、洗練する

Using either of these strategies, we repeatedly test until a root cause is identified, at which point we can then take corrective action to prevent a recurrence and write a postmortem.

これらの戦略のどちらか一方を使用して、根本の原因が特定されるまで、我々は繰り返し試験します。
それから、我々は再発防止のために、矯正するアクションを取ることができる。そして事後の検討事項を記述する

Of course, fixing the proximate cause(s) needn’t always wait for root-causing or postmortem writing.

もちろん、直接の原因を修正することは、根本原因、あるいは事後の検討をいつも待つ必要がない。

####  COMMON PITFALLS

陥りやすい落とし穴

Ineffective troubleshooting sessions are plagued by problems at the Triage, Examine, and Diagnose steps, often because of a lack of deep system understanding.

効果のないトラブルシューティングの会議は、しばしば深いシステムに対する理解不足のため
トリアージ（優先順位？）の問題に悩まされて、調査し、手順を診断する

The following are common pitfalls to avoid:

避けるための共通的な落とし穴には、次のとおりです。

* Looking at symptoms that aren’t relevant or misunderstanding the meaning of system metrics. Wild goose chases often result.

関連していない症状を見て、あるいはシステム・メトリックの意味を誤解。
無駄な追跡がしばしば生じます。

* Misunderstanding how to change the system, its inputs, or its environment, so as to safely and effectively test hypotheses.

安全かつ効果的に仮説を検証するために、システム、その入力、またはその環境を変更する方法を誤解。

* Coming up with wildly improbable theories about what’s wrong, or latching on to causes of past problems, reasoning that since it happened once, it must be happening again.

間違っていることについてひどくありそうもない理論を出す
あるいは、過去の問題の原因をとらわれ、
それはかつて起こったので、それが再び起こってしなければならないことを推論する

* Hunting down spurious correlations that are actually coincidences or are correlated with shared causes.

実際に偶然の一致、または共有の原因と相関している嘘の相関関係を追い詰める

Fixing the first and second common pitfalls is a matter of learning the system in question and becoming experienced with the common patterns used in distributed systems.  

第一および第二の一般的な落とし穴を修正することは、問題のシステムを学習し、分散システムで使用される共通のパターンに経験豊富になること

The third trap is a set of logical fallacies that can be avoided by remembering that not all failures are equally probable
— as doctors are taught, “when you hear hoofbeats, think of horses not zebras.”

第三のトラップは、すべての失敗が等しくありえる。というわけではないのを思い出すことによって、避けられることができる論理的誤りの一セットです
- 医者として教えられる「あなたが蹄の音を聞くとき、馬はシマウマではないと考える」

Also remember that, all things being equal, we should prefer simpler explanations.

また、すべてのことが等しいなら、私たちはより単純な説明を好むべきなのを思い出す

Finally, we should remember that correlation is not causation:

最後に、我々はその相関関係は因果関係ではないことを覚えておいてください

some correlated events, say packet loss within a cluster and failed hard drives in the cluster, share common causes

クラスタ内のパケット損失やクラスタ内のハードドライブの失敗といった、いくつかの相関関係のあるイベントは共通の原因を分け合う

 — in this case, a power outage, though network failure clearly doesn’t cause the hard drive failures nor vice versa.

このケース、停電では、ネットワーク失敗は、ハードディスク故障もその逆も明らかに引き起こさない

Even worse, as systems grow in size and complexity and as more metrics are monitored, it’s inevitable that there will be events that happen to correlate well with other events, purely by coincidence.

更に悪いことに、システムの規模と複雑さが成長し、より多くのメトリックスが監視されているとして、
それは純粋に偶然、他のイベントとよく相関するイベントが存在することは避けられない

Understanding failures in our reasoning process is the first step to avoiding them and becoming more effective in solving problems.

我々の推理プロセスの失敗を理解することは、それを避けて、問題を解決することにおいて、効果的になることへの第一歩です

A methodical approach to knowing what we do know, what we don’t know, and what we need to know, makes it simpler and more straightforward to figure out what’s gone wrong and how to fix it.

何がうまくいかなかったか、そして、どのようにそれを修正するべきか理解するために、
私たちが知ること、わからないこと、知る必要があること、を知る規則的なアプローチは
それをよりシンプルに、簡単にさせる　

## In Practice

実際には

In practice, of course, troubleshooting is never as clean as our idealized model suggests it should be.

実際には、もちろん、トラブルシューティングは、私たちの理想モデルとして、そうあるべきと示唆するほどクリアーにはならない。

There are some steps that can make the process less painful and more productive for both those experiencing system problems and those responding to them.

システムの問題を経験しているそれら、彼らに返事しているそれら両方のために、
より苦痛の少ない、より生産性が向上するプロセスを作ることができるいくつかのステップがあります。

### Problem Report

問題の報告

Every problem starts with a problem report, which might be an automated alert or one of your colleagues saying, “The system is slow.”

すべての問題は、問題のレポートから始まる
自動的なアラート、あるいは、あなたの同僚が「システムが遅い」といった発言かもしれない

An effective report should tell you the expected behavior, the actual behavior, and, if possible, how to reproduce the behavior.

効果的レポートは、期待される事象と実際の事象と、できれば、事象を再現する方法を教えなければなりません。

Ideally, the reports should have a consistent form and be stored in a searchable location, such as a bug tracking system.

理想的には、レポートに一貫性のある形を持っている必要があり、バグトラッキングシステムのような検索可能な場所に格納すること。

Here, our teams often have customized forms or small web apps that ask for information that’s relevant to diagnosing the particular systems they support, which then automatically generate and route a bug.

ここでは、私たちのチームは、しばしば、カスタマイズされたフォーム、
あるいはサポートする特定のシステムを診断するため、関連する情報を尋ねる小さなweb appsを持つ
そして、それから、自動的に生成し、バグを発送する

This may also be a good point at which to provide tools for problem reporters to try self-diagnosing or self-repairing common issues on their own.

これは、自己診断や共通の課題を自己修復するのに、問題のレポータのツールを提供するために、これで良い点かもしれません。

It’s common practice at Google to open a bug for every issue, even those received via email or instant messaging.

！！！
これは、電子メールやインスタントメッセージを介して受信したものであっても、すべての問題のためにバグを開くためにGoogleのが一般的です。

Doing so creates a log of investigation and remediation activities that can be referenced in the future.

そうすることで、将来的に参照することができ、調査と修復活動のログを作成します。

Many teams discourage reporting problems directly to a person for several reasons:

多くのチームはいくつかの理由のために人に直接問題を報告する思いとどまら：

this practice introduces an additional step of transcribing the report into a bug, produces lower-quality reports that aren’t visible to other members of the team, and tends to concentrate the problem-solving load on a handful of team members that the reporters happen to know, rather than the person currently on duty (see also Chapter 29).

この練習では、バグにレポートを転写する追加のステップを紹介し、チームの他のメンバーに表示されていない低品質のレポートを生成し、問題を集中する傾向記者はむしろ義務で現在の人よりも、知ることが起こるのチームメンバーの一握りの負荷を-solving（も参照の第29章を）。

Shakespeare Has a Problem

シェイクスピアは問題があります

You’re on-call for the Shakespeare search service and receive an alert, Shakespeare-BlackboxProbe_SearchFailure:

あなたは、シェイクスピアの検索サービスのためにコールしているとアラートを受信 Shakespeare-BlackboxProbe_SearchFailure：

your black-box monitoring hasn’t been able to find search results for “the forms of things unknown” for the past five minutes.

あなたのブラックボックスの監視では、過去5分間の「未知のものの形"の検索結果を見つけることができませんでした。

The alerting system has filed a bug—with links to the black-box prober’s recent results and to the playbook entry for this alert—and assigned it to you. Time to spring into action!

警告システムはバグとブラックボックスプローバの最近の結果にこのアラートとそれをあなたに割り当てられたために脚本のエントリへのリンクを提出しました。行動に春までの時間！

Triage

トリアージ

Once you receive a problem report, the next step is to figure out what to do about it.

あなたは問題報告書を受け取ったら、次のステップはそれについて何をすべきかを把握することです。

Problems can vary in severity: an issue might affect only one user under very specific circumstances (and might have a workaround), or it might entail a complete global outage for a service.

問題は非常に特殊な状況下で一つだけのユーザーに影響を与える可能性がある（と回避策を持っているかもしれません）、またはそれは、サービスの完全なグローバル停止を伴う可能性があります：問題が深刻度を変えることができます。

Your response should be appropriate for the problem’s impact: it’s appropriate to declare an all-hands-on-deck emergency for the latter (see Chapter 14), but doing so for the former is overkill. Assessing an issue’s severity requires an exercise of good engineering judgment and, often, a degree of calm under pressure.

あなたの応答は、問題の影響のために適切であるべきである：それは（参照、後者のためのすべてのハンズオンデッキ緊急事態を宣言するために、適切なだ第14章）が、前者のためにそうすることは行き過ぎです。問題の深刻度を評価することは、多くの場合、下の穏やかな程度の良好な技術的判断の行使を必要とし、 圧力を。

Your first response in a major outage may be to start troubleshooting and try to find a root cause as quickly as possible. Ignore that instinct!

主要な停電であなたの最初の応答は、トラブルシューティングを開始し、可能な限り迅速に根本的な原因を見つけようとすることであってもよいです。その本能を無視！

Instead, your course of action should be to make the system work as well as it can under the circumstances.

代わりに、アクションのコースをすることであるべき状況下でも同様にそれができるようなシステムを機能させます。

This may entail emergency options, such as diverting traffic from a broken cluster to others that are still working, dropping traffic wholesale to prevent a cascading failure, or disabling subsystems to lighten the load. Stopping the bleeding should be your first priority; you aren’t helping your users if the system dies while you’re root-causing.

これは、このような、まだ作業している他の人に壊れたクラスタからのトラフィックを迂回させるカスケード故障を防止するために、トラフィックの卸売をドロップ、または負荷を軽減するためのサブシステムを無効にするなどの緊急オプションを伴ってもよいです。出血を停止すると、あなたの最優先事項であるべきです。あなたはルート原因している間、システムが死んだ場合、あなたのユーザーを支援していません。

Of course, an emphasis on rapid triage doesn’t preclude taking steps to preserve evidence of what’s going wrong, such as logs, to help with subsequent root-cause analysis.

もちろん、迅速なトリアージ上の重点は、その後の根本原因分析を支援するために、ログなど、間違って何が起こっているかの証拠を維持するための手順を取って排除するものではありません。

Novice pilots are taught that their first responsibility in an emergency is to fly the airplane [Gaw09]; troubleshooting is secondary to getting the plane and everyone on it safely onto the ground.

初心者パイロットは緊急時には彼らの最初の責任は飛行機飛ぶことであることを教えられている[Gaw09]を。トラブルシューティングは、その上に平面と皆を得るために二次的なものである安全に地面に。

This approach is also applicable to computer systems: for example, if a bug is leading to possibly unrecoverable data corruption, freezing the system to prevent further failure may be better than letting this behavior continue.

このアプローチは、コンピュータシステムにも適用可能である：例えば、バグが継続し、この動作をさせるよりも良いかもしれさらに故障を防止するためのシステムを凍結し、おそらく回復不能なデータ破壊につながっている場合。

This realization is often quite unsettling and counterintuitive for new SREs, particularly those whose prior experience was in product development organizations.

この認識は、しばしば非常に不安と新しいのSRE、その経験製品開発組織にあった、特にそれらのための直感に反します。

Examine

調べます

We need to be able to examine what each component in the system is doing in order to understand whether or not it’s behaving correctly.

我々は、システム内の各構成要素は、それが正しく動作していたか否かを理解するために行っていることを検査できるようにする必要があります。

Ideally, a monitoring system is recording metrics for your system as discussed in Chapter 10.

で説明したように理想的には、監視システムは、システムのメトリックを記録している第10章。

These metrics are a good place to start figuring out what’s wrong.

これらの指標は、何が間違って考え出す開始するには良い場所です。

Graphing time-series and operations on time-series can be an effective way to understand the behavior of specific pieces of a system and find correlations that might suggest where problems began.9

時系列の時系列や操作をグラフ化すると、システムの特定部分の動作を理解し、問題が始まった場所を示唆している可能性がある相関関係を見つけるための効果的な方法です。9

Logging is another invaluable tool.

ロギングは別の非常に貴重なツールです。

Exporting information about each operation and about system state makes it possible to understand exactly what a process was doing at a given point in time.

各操作について、システムの状態に関する情報をエクスポートすると、プロセスが任意の時点でやっていた正確に理解することができます。

You may need to analyze system logs across one or many processes. Tracing requests through the whole stack using tools such as Dapper [Sig10] provides a very powerful way to understand how a distributed system is working, though varying use cases imply significantly different tracing designs [Sam14].

あなたは、1または複数のプロセス間でのシステムログを分析する必要があるかもしれません。このようなDapperのようなツールを使用してスタック全体を通じて要求をトレース[SIG10] 様々なユースケースが著しく異なるトレースのデザインを意味するものではありますが、分散システムが機能しているかを理解するための非常に強力な方法を提供します[Sam14] 。

LOGGING

ロギング

Text logs are very helpful for reactive debugging in real time, while storing logs in a structured binary format can make it possible to build tools to conduct retrospective analysis with much more information.

構造化されたバイナリ形式でログを格納することはより多くの情報とレトロスペクティブ分析を行うためのツールを構築することを可能にすることができますが、テキストログは、リアルタイムで反応デバッグのために非常に役に立ちます。

It’s really useful to have multiple verbosity levels available, along with a way to increase these levels on the fly. This functionality enables you to examine any or all operations in incredible detail without having to restart your process, while still allowing you to dial back the verbosity levels when your service is operating normally.

それは、オンザフライでこれらのレベルを増加させるための方法に沿って利用可能な複数の詳細レベルを、持っていることは本当に便利です。この機能はまだあなたのサービスが正常に動作しているときに、詳細レベルをバックにダイヤルすることを可能にしながら、あなたのプロセスを再起動することなく、信じられないほど詳細にいずれかまたはすべての操作を調べることができます。

Depending of the volume of traffic your service receives, it might be better to use statistical sampling; for example, you might show one out of every 1,000 operations.

あなたのサービスが受信したトラフィックの量に依存し、統計的サンプリングを使用する方がよいかもしれません。たとえば、1,000の操作のうちいずれかを表示することがあります。

A next step is to include a selection language so that you can say “show me operations that match X,” for a wide range of X—e.g., Set RPCs with a payload size below 1,024 bytes, or operations that took longer than 10 ms to return, or which called doSomethingInteresting() in rpc_handler.py.

次のステップは、X-例えば、広い範囲のために」、私はXに一致する操作を示し、「あなたが言うことができるように、選択言語を含めることであるSet により長い10ミリ秒を要した1024バイト、または操作以下のペイロードサイズとのRPC返す、またはと呼ばれる doSomethingInteresting()にrpc_handler.py。

You might even want to design your logging infrastructure so that you can turn it on as needed, quickly and selectively.

必要に応じて、迅速かつ選択的に、それをオンにすることができるようにあなたもあなたのロギングインフラストラクチャを設計することができます。

Exposing current state is the third trick in our toolbox.

現在の状態を公開すること、当社のツールボックス第三トリックです。

For example, Google servers have endpoints that show a sample of RPCs recently sent or received, so it’s possible to understand how any one server is communicating with others without referencing an architecture diagram.

たとえば、Googleのサーバーは、RPCのサンプルが最近送信または受信を示すエンドポイントを持っているので、アーキテクチャ図を参照することなく、いずれかのサーバが他の人と通信しているかを理解することが可能です。

These endpoints also show histograms of error rates and latency for each type of RPC, so that it’s possible to quickly tell what’s unhealthy. Some systems have endpoints that show their current configuration or allow examination of their data; for instance, Google’s Borgmon servers (Chapter 10) can show the monitoring rules they’re using, and even allow tracing a particular computation step-by-step to the source metrics from which a value is derived.

それはすぐに不健康何伝えることが可能ですように、これらのエンドポイントはまた、RPCの各タイプのエラー率と待ち時間のヒストグラムを示しています。一部のシステムでは、彼らの現在の設定を表示したり、データの検査を可能にするエンドポイントを持っています。例えば、GoogleのBorgmonサーバ（第10章）、彼らが使用している監視ルールを示し、さらには特定の計算ステップ・バイ・ステップの値が由来するソース・メトリックへのトレースを許可することができます。

Finally, you may even need to instrument a client to experiment with, in order to discover what a component is returning in response to requests.

最後に、あなたも、コンポーネントが要求に応じて返しているものを発見するために、機器にして実験するクライアントが必要な場合があります。

DEBUGGING SHAKESPEARE

デバッグシェイクスピア

Using the link to the black-box monitoring results in the bug, you discover that the prober sends an HTTP GET request to the /api/search endpoint:

バグでブラックボックスモニタリング結果へのリンクを使用して、プローバがにHTTP GETリクエストを送信することを発見し /api/searchたエンドポイント：

{
  ‘search_text’: ‘the forms of things unknown’
}

It expects to receive a response with an HTTP 200 response code and a JSON payload exactly matching:

これは、HTTP 200応答コードと正確に一致するJSONペイロードを持つレスポンスを受信することを期待します：

[{
	"work": "A Midsummer Night's Dream",
	"act": 5,
	"scene": 1,
	"line": 2526,
	"speaker": "Theseus"
}]

The system is set up to send a probe once a minute; over the past 10 minutes, about half the probes have succeeded, though with no discernible pattern.

システムは、1分後にプローブを送信するように設定されています。過去10分間かけて、約半分のプローブは、識別可能なパターンを持つものの、成功しています。

Unfortunately, the prober doesn’t show you what was returned when it failed; you make a note to fix that for the future.

残念ながら、プローバはあなたが表示されない もの、それが失敗したときに返されました。あなたは将来のためにそれを修正するためにメモしておきます。

Using curl, you manually send requests to the search endpoint and get a failed response with HTTP response code 502 (Bad Gateway) and no payload.

使用してcurl、手動で検索エンドポイントにリクエストを送信し、HTTPレスポンスコード502（不正なゲートウェイ）と無ペイロードを使用して、失敗した応答を得ます。

It has an HTTP header, X-Request-Trace, which lists the addresses of the backend servers responsible for responding to that request. With this information, you can now examine those backends to test whether they’re responding appropriately.

これは、HTTPヘッダー、持ってX-Request-Traceその要求に応答する責任のバックエンドサーバのアドレスを示しています、。この情報により、あなたは今、彼らは適切に対応しているかどうかをテストするために、これらのバックエンドを調べることができます。

Diagnose

診断します

A thorough understanding of the system’s design is decidedly helpful for coming up with plausible hypotheses about what’s gone wrong, but there are also some generic practices that will help even without domain knowledge.

システムの設計の十分な理解が間違っているものについてのもっともらしい仮説を考え出すために明らかに役立ちますが、でも、ドメイン知識なしで役立ついくつかの一般的な慣行もあります。

SIMPLIFY AND REDUCE

簡素化と削減

Ideally, components in a system have well-defined interfaces and perform known transformations from their input to their output (in our example, given an input search text, a component might return output containing possible matches).

理想的には、システム内のコンポーネントは、（この例では、入力された検索テキストを指定して、コンポーネントが可能なマッチを含む出力を返す場合があります）、明確に定義されたインタフェースを持っており、それらの出力にその入力から知られている変換を行います。

It’s then possible to look at the connections between components—or, equivalently, at the data flowing between them—to determine whether a given component is working properly.

これは、接続を見てすることが可能だとの間に特定のコンポーネントが正常に動作しているかどうかを判断するためにそれら-間を流れるデータで、同等の構成要素-または、。

Injecting known test data in order to check that the resulting output is expected (a form of black-box testing) at each step can be especially effective, as can injecting data intended to probe possible causes of errors.

データを注入缶エラーの原因を調べるために意図したように各ステップで得られた出力が期待されていること（ブラックボックステストの形）を確認するために、既知のテストデータを注入することは、特に有効であることができます。

Having a solid reproducible test case makes debugging much faster, and it may be possible to use the case in a non-production environment where more invasive or riskier techniques are available than would be possible in production.

固体再現可能なテストケースを有するはるかに速くデバッグなり、生産に可能であるよりもより侵襲的またはリスクの高い技術が利用可能である、非運用環境でケースを使用することも可能です。

Dividing and conquering is a very useful general-purpose solution technique.

分割して征服することは非常に便利な汎用解法です。

In a multilayer system where work happens throughout a stack of components, it’s often best to start systematically from one end of the stack and work toward the other end, examining each component in turn.

作業は、コンポーネントのスタック全体で起こる多層システムでは、スタックの一端から体系的に開始して、順番に各コンポーネントを調べ、他の端に向かって作業することが多いのが最善です。

This strategy is also well-suited for use with data processing pipelines.

この戦略は、データ処理パイプラインでの使用に適しています。

In exceptionally large systems, proceeding linearly may be too slow; an alternative, bisection, splits the system in half and examines the communication paths between components on one side and the other.

非常に大規模なシステムでは、直線的に進むことは遅すぎるかもしれません。代替、二分は、半分にシステムを分割し、一方の側のコンポーネントとその他の間の通信経路を調べます。

After determining whether one half seems to be working properly, repeat the process until you’re left with a possibly faulty component.

あなたはおそらく障害のあるコンポーネントが残っているまで、半分が正常に動作しているようだかどうかを判定した後、プロセスを繰り返します。

ASK “WHAT,” “WHERE,” AND “WHY”

掲載「何を」「どこで」、および「なぜ」

A malfunctioning system is often still trying to do something—just not the thing you want it to be doing.

誤動作システムは、多くの場合、まだやるしようとしている何かあなたはそれをやっているしたく-justない事を。

Finding out what it’s doing, then asking why it’s doing that and where its resources are being used or where its output is going can help you understand how things have gone wrong.

見つける何を求めて、その後、それはやっている理由、それはそれをやっているし、どこにそのリソースが使用されているか、その出力がどこへ行くのかはあなたが物事がうまく行っている方法を理解するのに役立つことができます。10

UNPACKING THE CAUSES OF A SYMPTOM

症状の原因を開梱

Symptom: A Spanner cluster has high latency and RPCs to its servers are timing out.

症状：スパナクラスタは、そのサーバーがタイムアウトしているに高遅延およびRPCを持っています。

Why? The Spanner server tasks are using all their CPU time and can’t make progress on all the requests the clients send.

なぜ？スパナサーバタスクは、すべてのCPU時間を使用していて、クライアントが送信するすべての要求に進展することはできません。

Where in the server is the CPU time being used? Profiling the server shows it’s sorting entries in logs checkpointed to disk.

どこで CPU時間は、サーバで使用されていますか？サーバーをプロファイリングすることは、ディスクにチェックポイントログにエントリを並べ替えています示しています。

Where in the log-sorting code is it being used? When evaluating a regular expression against paths to log files.

どこログソートコードでそれが使用されていますか？ログファイルへのパスに対して正規表現を評価するとき。

Solutions: Rewrite the regular expression to avoid backtracking. Look in the codebase for similar patterns. Consider using RE2, which does not backtrack and guarantees linear runtime growth with input size.11

ソリューション：バックトラッキングを回避するために正規表現を書き換えます。同様のパターンのためのコードベースで見てください。バックトラックと入力サイズの線形ランタイム成長を保証しませんRE2を、使用することを検討してください。11

WHAT TOUCHED IT LAST

どのような最後のそれに触れ

Systems have inertia: we’ve found that a working computer system tends to remain in motion until acted upon by an external force, such as a configuration change or a shift in the type of load served.

システムは慣性を持っている：我々は作業コンピュータシステムは、このような構成変更または務め負荷の種類のシフトとして、外力が作用するまでの動きに残る傾向にあることがわかりました。

Recent changes to a system can be a productive place to start identifying what’s going wrong.12

システムへの最近の変更は間違って何が起こっているのか特定を開始する生産場所にすることができます。12

Well-designed systems should have extensive production logging to track new version deployments and configuration changes at all layers of the stack, from the server binaries handling user traffic down to the packages installed on individual nodes in the cluster.

よく設計されたシステムは、サーバのバイナリがダウンしてクラスタ内の個々のノードにインストールされたパッケージへのユーザートラフィックを処理するから、スタックのすべての層で、新しいバージョンの展開と構成の変更を追跡するために大規模な生産のログを持っている必要があります。

Correlating changes in a system’s performance and behavior with other events in the system and environment can also be helpful in constructing monitoring dashboards; for example, you might annotate a graph showing the system’s error rates with the start and end times of a deployment of a new version, as seen in Figure 12-2

また、監視ダッシュボードを構築するのに役立つことができるシステムおよび環境内の他のイベントと、システムのパフォーマンスと行動の変化を相関。見られるように、たとえば、あなたは、新しいバージョンの展開の開始時刻と終了時刻とシステムの誤り率を示すグラフに注釈を付けることがあります 。図12-2。

Manually sending a request to the /api/search endpoint (see “Debugging Shakespeare”) and seeing the failure listing backend servers that handled the response lets you discount the likelihood that the problem is with the API frontend server and with the load balancers: the response probably wouldn’t have included that information if the request hadn’t at least made it to the search backends and failed there. Now you can focus your efforts on the backends—analyzing their logs, sending test queries to see what responses they return, and examining their exported metrics.

手動にリクエスト送信/api/searchエンドポイント（参照「デバッグシェークスピアの"）と応答を処理し、バックエンドサーバをリストの失敗を見てすることで、問題がAPIのフロントエンドサーバーとし、ロードバランサであるという可能性割り引くことができます： 'wouldnおそらく応答を要求は、少なくとも検索バックエンドにそれを作って、そこ失敗していなかった場合tは、その情報が含まれています。今、あなたは、彼らのログをバックエンド・分析し、彼らが返すものを応答確認するためにテストクエリを送信し、そのエクスポートされた指標を検討する上であなたの努力を集中することができます。

SPECIFIC DIAGNOSES

具体的な診断

While the generic tools described previously are helpful across a broad range of problem domains, you will likely find it helpful to build tools and systems to help with diagnosing your particular services.

先に述べた一般的なツールは、問題領域の広い範囲にわたって有用ですが、あなたは、おそらくそれが参考にあなたの特定のサービスの診断を支援するツールやシステムを構築することができます。

Google SREs spend much of their time building such tools.

GoogleサービスのSREは、そのようなツールを構築する彼らの時間の大半を費やしています。

While many of these tools are necessarily specific to a given system, be sure to look for commonalities between services and teams to avoid duplicating effort.

これらのツールの多くは、特定のシステムに必ずしも特定のですが、努力の重複を避けるため、サービスとチーム間の共通点を探すようにしてください。

Test and Treat

テストとトリート

Once you’ve come up with a short list of possible causes, it’s time to try to find which factor is at the root of the actual problem.

あなたは可能性のある原因の短いリストを作ってみた後は、見つけようとする時が来ている実際の問題の根底にある要因。

Using the experimental method, we can try to rule in or rule out our hypotheses.

実験方法を使用して、我々は内のルールや私たちの仮説を除外しようとすることができます。

For instance, suppose we think a problem is caused by either a network failure between an application logic server and a database server, or by the database refusing connections.

たとえば、私たちは問題がアプリケーション・ロジック・サーバーとデータベース・サーバー間のネットワーク障害のいずれかによって、またはデータベース拒否接続によって引き起こされていると思うと仮定します。

Trying to connect to the database with the same credentials the application logic server uses can refute the second hypothesis, while pinging the database server may be able to refute the first, depending on network topology, firewall rules, and other factors.

データベースサーバをpingがネットワークトポロジ、ファイアウォールルール、およびその他の要因に応じて、最初に異議を唱えることができる場合もありますが、アプリケーション・ロジック・サーバーが使用する同じ資格情報を使用してデータベースに接続しようとすると、第二の仮説を論破することができます。

Following the code and trying to imitate the code flow, step-by-step, may point to exactly what’s going wrong.

次のコードとコードの流れを模倣しようとすると、ステップバイステップで、間違って起こっているのに正確に何を指す場合があります。


There are a number of considerations to keep in mind when designing tests (which may be as simple as sending a ping or as complicated as removing traffic from a cluster and injecting specially formed requests to find a race condition):

（クラスタからのトラフィックを削除し、競合状態を見つけるために特別に形成されたリクエストを注入するようにpingを送信するのと同じくらい簡単かのように複雑でもよい）のテストを設計する際に留意すべき検討事項がいくつかあります：

* An ideal test should have mutually exclusive alternatives, so that it can rule one group of hypotheses in and rule another set out. In practice, this may be difficult to achieve.

それはで仮説の一つのグループを支配し、別のセットを除外することができるように、理想的なテストは、相互に排他的な選択肢を持っている必要があります。実際には、これは達成することは困難であり得ます。

* Consider the obvious first: perform the tests in decreasing order of likelihood, considering possible risks to the system from the test. It probably makes more sense to test for network connectivity problems between two machines before looking into whether a recent configuration change removed a user’s access to the second machine.

明白な最初のを考えてみましょう：テストからシステムへの可能性のあるリスクを考慮し、可能性の順に試験を行います。それはおそらく、最近の構成変更が第二のマシンへのユーザーのアクセスを削除するかどうかを検討して前に2つのマシン間のネットワーク接続の問題をテストするために、より理にかなっています。

* An experiment may provide misleading results due to confounding factors. For example, a firewall rule might permit access only from a specific IP address, which might make pinging the database from your workstation fail, even if pinging from the application logic server’s machine would have succeeded.

実験は、交絡因子に起因する誤った結果を提供することができます。たとえば、ファイアウォールルールがワークステーションからデータベースにpingを実行するかもしれないこれは、唯一の特定のIPアドレスからのアクセスを許可する可能性があるアプリケーション・ロジック・サーバーのマシンからpingを実行が成功したであろうとしても、失敗します。

* Active tests may have side effects that change future test results. For instance, allowing a process to use more CPUs may make operations faster, but might increase the likelihood of encountering data races. Similarly, turning on verbose logging might make a latency problem even worse and confuse your results: is the problem getting worse on its own, or because of the logging?

アクティブテストは、将来のテストの結果を変更する副作用を有することができます。例えば、プロセスはより多くのCPUを使用できるようにすると、操作がより速く行うことができるが、データ競合が発生する可能性を高める可能性があります。同様に、詳細なログ記録をオンにすると、待ち時間の問題をさらに悪化させるとあなたの結果を混乱させる可能性があります：問題が独自に悪化している、またはためのロギングの？

* Some tests may not be definitive, only suggestive. It can be very difficult to make race conditions or deadlocks happen in a timely and reproducible manner, so you may have to settle for less certain evidence that these are the causes.

いくつかのテストが唯一の示唆に富む、決定的ではないかもしれません。あなたはこれらが原因であることが少なく、特定の証拠のために解決する必要がありますので、競合状態やデッドロックがタイムリーかつ再現可能な方法で実現することは非常に困難な場合があります。

Take clear notes of what ideas you had, which tests you ran, and the results you saw.13 Particularly when you are dealing with more complicated and drawn-out cases, this documentation may be crucial in helping you remember exactly what happened and prevent having to repeat these steps.14

あなたが実行したテストあなたが持っていたアイデア、そしてあなたが見た結果の明確なメモを取る。13あなたはより複雑で引き出された例を扱っている。特に、このドキュメントでは、何が起こったかを正確に覚えているとのを防ぐうえで非常に重要であってもよいですこれらの手順を繰り返す。

If you performed active testing by changing a system—for instance by giving more resources to a process—making changes in a systematic and documented fashion will help you return the system to its pre-test setup, rather than running in an unknown hodge-podge configuration.

14を  使用すると、アクティブなテストを実行した場合、システムのインスタンスを変更することによって、あなたはその前のテスト・セットアップにシステムを戻すのに役立ちます体系的かつ文書化された方式でプロセス作りの変化に、より多くのリソースを与えるのではなく、実行することで、未知のホッジ-ずんぐりした人の設定インチ

Negative Results Are Magic

負の結果がマジックです

Written by Randall Bosetti

Edited by Joan Wendt

ランドールBosettiによって書かれました

ジョーン・ウェントによって編集

A “negative” result is an experimental outcome in which the expected effect is absent—that is, any experiment that doesn’t work out as planned. This includes new designs, heuristics, or human processes that fail to improve upon the systems they replace.

「陰性」の結果は、期待される効果は存在しない、つまり、計画通りにうまくいかない任意の実験であるとした実験結果です。これは、彼らが交換するシステムを改良するために失敗し、新しいデザイン、ヒューリスティック、またはヒトのプロセスを含みます。

Negative results should not be ignored or discounted. Realizing you’re wrong has much value: a clear negative result can resolve some of the hardest design questions. Often a team has two seemingly reasonable designs but progress in one direction has to address vague and speculative questions about whether the other direction might be better.

。否定的な結果を無視または割引されるべきではない、あなたは間違っている実現するために多くの価値を持っている：明確な否定的な結果は、最も困難な設計上の問題の一部を解決することができます。多くの場合、チームは2一見合理的なデザインを持っているが、1つの方向に進展が他の方向が良いかもしれないかどうかについて曖昧で投機的な質問に対応しなければなりません。

Experiments with negative results are conclusive.

その結果、陰性で実験が決定的である。

They tell us something certain about production, or the design space, or the performance limits of an existing system.

彼らは私たちの生産、または設計空間、または既存のシステムの性能限界について特定の何かを伝えます。

They can help others determine whether their own experiments or designs are worthwhile.

彼らは他の人が自分の実験やデザインは価値があるかどうかを判断するのに役立ちます。

For example, a given development team might decide against using a particular web server because it can handle only ~800 connections out of the needed 8,000 connections before failing due to lock contention.

例えば、与えられた開発チームは、それが競合をロックすることにより、失敗する前に必要な8000の接続のうちだけ〜800の接続を処理できるため、特定のWebサーバーを使用しないことを決めるかもしれません。

When a subsequent development team decides to evaluate web servers, instead of starting from scratch, they can use this already well-documented negative result as a starting point to decide quickly whether (a) they need fewer than 800 connections or (b) the lock contention problems have been resolved.

その後の開発チームではなく、最初からの、Webサーバを評価することを決定したときは、（a）は、彼らがより少ない800接続または（b）のロックが必要かどうかを迅速に決定するための出発点として、この既に十分に立証された否定的な結果を使用することができます競合の問題が解決されました。

Even when negative results do not apply directly to someone else’s experiment, the supplementary data gathered can help others choose new experiments or avoid pitfalls in previous designs.

陰性の結果が誰かの他の人の実験に直接適用されない場合でも、他の人を助けることができ集め補足データは、新しい実験を選択するか、以前の設計で落とし穴を避けます。

Microbenchmarks, documented antipatterns, and project postmortems all fit this category.

マイクロベンチマーク、文書化アンチパターン、およびプロジェクトpostmortemsすべてこのカテゴリに合います。

You should consider the scope of the negative result when designing an experiment, because a broad or especially robust negative result will help your peers even more.

実験を設計する際に広いまたは特に堅牢な負の結果は、さらに仲間を助けるので、あなたは、否定的な結果の範囲を検討する必要があります。

Tools and methods can outlive the experiment and inform future work. As an example, benchmarking tools and load generators can result just as easily from a disconfirming experiment as a supporting one.

ツールと方法は、実験よりも長生きし、今後の活動を知らせることができる。 例として、支援一つとしてdisconfirming実験から同じように簡単にツールや負荷ジェネレータが生じる可能性がベンチマーク。

Many webmasters have benefited from the difficult, detail-oriented work that produced Apache Bench, a web server loadtest, even though its first results were likely disappointing.

多くのウェブマスターは、その最初の結果は、おそらく失望したにも関わらず、Apacheのベンチ、Webサーバのloadtestを生産困難、詳細志向の仕事の恩恵を受けています。

Building tools for repeatable experiments can have indirect benefits as well: although one application you build may not benefit from having its database on SSDs or from creating indices for dense keys, the next one just might.

反復可能な実験のための建物のツールにも間接的なメリットを持つことができます：あなたが構築する一つのアプリケーションでは、SSDの上または密なキーのインデックスを作成するから、そのデータベースを持っていることから、次の1だけかもしれないの利益にないかもしれません。

Writing a script that allows you to easily try out these configuration changes ensures you don’t forget or miss optimizations in your next project.

あなたは簡単にこれらの設定変更を試してみることができ、スクリプトを書くことは、あなたの次のプロジェクトに最適化を忘れたり、お見逃しなく保証します。

Publishing negative results improves our industry’s data-driven culture.

陰性の結果を公開すると、我々の業界のデータ駆動型の文化を向上させる。

Accounting for negative results and statistical insignificance reduces the bias in our metrics and provides an example to others of how to maturely accept uncertainty.

陰性結果の会計処理と統計取るに足りないが、私たちのメトリックにバイアスを低減し、分別の不確実性を受け入れるする方法の他に例を提供します。

By publishing everything, you encourage others to do the same, and everyone in the industry collectively learns much more quickly.

すべてを公開することで、あなたは同じことを行うために他の人を励まし、そして業界の誰もが一括してはるかに迅速に学習します。

SRE has already learned this lesson with high-quality postmortems, which have had a large positive effect on production stability.

SREは、すでに生産安定性に大きなプラスの効果を持っていた、高品質のpostmortems、この教訓を学びました。

Publish your results. If you are interested in an experiment’s results, there’s a good chance that other people are as well.

あなたの結果を公開します。あなたは、実験の結果に興味がある場合は、他の人が同様であることを良いチャンスがあります。

When you publish the results, those people do not have to design and run a similar experiment themselves.

あなたは結果を公開すると、それらの人々は、自分自身を同様の実験を設計し、実行する必要はありません。

It’s tempting and common to avoid reporting negative results because it’s easy to perceive that the experiment “failed.” Some experiments are doomed, and they tend to be caught by review.

これは、いくつかの実験が運命にある」。失敗した」魅力的な、それは実験がいることを知覚するのは簡単ですので、陰性の結果を報告して回避することが一般的だし、彼らは、レビューによって捕捉される傾向にあります。

Many more experiments are simply unreported because people mistakenly believe that negative results are not progress.

人々が誤って陰性の結果が進行中でないことを信じているので、多くのより多くの実験が簡単に報告されていないです。

Do your part by telling everyone about the designs, algorithms, and team workflows you’ve ruled out.

あなたは除外しまし設計、アルゴリズム、およびチームのワークフローについて皆に伝えることであなたの部分を実行してください。

Encourage your peers by recognizing that negative results are part of thoughtful risk taking and that every well-designed experiment has merit.

陰性の結果が思慮深いリスクテイクの一部であり、そのすべてのうまく設計された実験はメリットがあることを認識することによって、あなたの仲間を奨励します。

Be skeptical of any design document, performance review, or essay that doesn’t mention failure.

失敗を言及していない任意の設計ドキュメント、人事考課、またはエッセイの懐疑的です。

Such a document is potentially either too heavily filtered, or the author was not rigorous in his or her methods.

このような文書は、潜在的にどちらか過度にろ過する、または著者は彼または彼女の方法に厳格ではありませんでした。

Above all, publish the results you find surprising so that others—including your future self—aren’t surprised.

何より、あなたの将来の自己aren't驚い他人-含むようにあなたは驚くべき見つける結果を公開。

Cure

治す

Ideally, you’ve now narrowed the set of possible causes to one. Next, we’d like to prove that it’s the actual cause.

理想的には、あなたが今1に考えられる原因のセットを狭めてきました。

Definitively proving that a given factor caused a problem—by reproducing it at will—can be difficult to do in production systems; often, we can only find probable causal factors, for the following reasons:

次に、我々はそれが実際の原因だということを証明したいと思います。決定的に与えられた要因があることを証明起因する問題を-ことにより、生産システムで行うことは困難であること、でき意志でそれを再現します。多くの場合、我々は見つけることができる 可能性の高い次の理由から、因果要因を、：

* Systems are complex. It’s quite likely that there are multiple factors, each of which individually is not the cause, but which taken jointly are causes.15 Real systems are also often path-dependent, so that they must be in a specific state before a failure occurs.

システムは複雑です。これは、共同で原因となっている個別の原因ではありませんが、その撮影したそのそれぞれが、複数の要因があることは非常に可能性があります。15 障害が発生する前に、彼らが特定の状態でなければなりませんように、実際のシステムは、また、多くの場合、パスに依存しています。

* Reproducing the problem in a live production system may not be an option, either because of the complexity of getting the system into a state where the failure can be triggered, or because further downtime may be unacceptable.

本番システムで問題を再現することは選択肢ではないかもしれない障害がトリガすることができる状態にシステムを得ることの複雑さのため、またはさらなるダウンタイムは容認できないかもしれないのでどちらか、

Having a nonproduction environment can mitigate these challenges, though at the cost of having another copy of the system to run.

。非本番環境を持つことは、実行するためのシステムの別のコピーを持つことのコストでいるが、これらの課題を軽減することができます。

Once you’ve found the factors that caused the problem, it’s time to write up notes on what went wrong with the system, how you tracked down the problem, how you fixed the problem, and how to prevent it from happening again.

あなたが問題を引き起こした要因を見つけたら、それはあなたが問題を修正する方法と、再び起きてからそれを防ぐために、どのように問題を追跡し、どのようにシステムに何が悪かったのかにメモを書くための時間です。

In other words, you need to write a postmortem (although ideally, the system is alive at this point!).

（理想的には、システムがあるが、言い換えれば、あなたは死後を記述する必要が生きているこの時点で！）。

Case Study

ケーススタディ

App Engine,16 part of Google’s Cloud Platform, is a platform-as-a-service product that allows developers to build services atop Google’s infrastructure.

App Engineを、16 Googleのクラウドプラットフォームの一部は、開発者はGoogleのインフラストラクチャの上にサービスを構築することを可能にするプラットフォームサービスとしての製品です。

One of our internal customers filed a problem report indicating that they’d recently seen a dramatic increase in latency, CPU usage, and number of running processes needed to serve traffic for their app, a content-management system used to build documentation for developers.17 The customer couldn’t find any recent changes to their code that correlated with the increase in resources, and there hadn’t been an increase in traffic to their app (see Figure 12-3), so they were wondering if a change in the App Engine service was responsible.

社内の顧客の1つは、最近、劇的な待ち時間の増加、CPU使用率、およびそれらのアプリ、開発者向けのドキュメントをビルドするために使用するコンテンツ管理システムのトラフィックを提供するために必要なプロセスを実行しているの数を見たいことを示す問題報告書を提出しました。17顧客が資源の増加と相関し、そのアプリ（参照へのトラフィックが増加していなかった自分のコードに任意の最近の変更を見つけることができませんでした図12-3）ので、変化があれば不思議に思っていましたApp Engineのサービスを担当していました。

Our investigation discovered that latency had indeed increased by nearly an order of magnitude (as shown in Figure 12-4). Simultaneously, the amount of CPU time (Figure 12-5) and number of serving processes (Figure 12-6) had nearly quadrupled. Clearly something was wrong.

私たちの調査では、待ち時間が実際に（に示すように大きさのほぼ順に増加していたことを発見図12-4）。同時に、CPU時間の量（図12-5）とサービングプロセスの数（図12-6）4倍近くありました。明らかに何かが間違っていました。

It was time to start troubleshooting.

トラブルシューティングを開始する時間でした。

Typically a sudden increase in latency and resource usage indicates either an increase in traffic sent to the system or a change in system configuration. However, we could easily rule out both of these possible causes: while a spike in traffic to the app around 20:45 could explain a brief surge in resource usage, we’d expect traffic to return to baseline fairly soon after request volume normalized. This spike certainly shouldn’t have continued for multiple days, beginning when the app’s developers filed the report and we started looking into the problem. Second, the change in performance happened on Saturday, when neither changes to the app nor the production environment were in flight. The service’s most recent code pushes and configuration pushes had completed days before. Furthermore, if the problem originated with the service, we’d expect to see similar effects on other apps using the same infrastructure. However, no other apps were experiencing similar effects.

典型的には、レイテンシおよびリソースの使用状況の急激な増加は、システムに送信されたトラフィックの増加やシステム構成の変更のいずれかを示しています。しかし、我々は簡単にこれらの可能性のある原因の両方を除外することができます：夜8時45周りのアプリへのトラフィックのスパイクは、リソース使用量の短いサージを説明することができる一方で、我々は、トラフィックがかなりすぐに要求量が正規化した後にベースラインに戻ることを期待したいです。このスパイクは確かにアプリの開発者が報告書を提出し、私たちが問題に探し始めたときから始まる、複数日間続けていてはいけません。アプリのどちらの変更も、本番環境が飛行していたときに、第2、パフォーマンスの変化は、土曜日に起こりました。サービスの最新のコードは、プッシュし、コンフィギュレーションプッシュは、前の日を完了しました。問題がサービスと発信された場合はさらに、我々は同じインフラストラクチャを使用して他のアプリにも同様の効果を見ることを期待したいです。しかし、他のアプリは、同様の効果を経験しませんでした。

We referred the problem report to our counterparts, App Engine’s developers, to investigate whether the customer was encountering any idiosyncrasies in the serving infrastructure. The developers weren’t able to find any oddities, either. However, a developer did notice a correlation between the latency increase and the increase of a specific data storage API call, merge_join, which often indicates suboptimal indexing when reading from the datastore. Adding a composite index on the properties the app uses to select objects from the datastore would speed those requests, and in principle, speed the application as a whole—but we’d need to figure out which properties needed indexing. A quick look at the application’s code didn’t reveal any obvious suspects.

我々は、顧客がサービスを提供し、インフラストラクチャ内の任意の特異性に遭遇したかどうかを調査するために、私たちのカウンターパート、App Engineの開発者に問題レポートを呼びます。開発者は、いずれかの、いずれかの奇妙を見つけることができませんでした。しかし、開発者は、待ち時間の増加、特定のデータ記憶APIコールの増加との相関関係に気付かなかったmerge_joinデータストアから読み取るとき、しばしば次善の索引付けを示しています。アプリはそれらの要求を高速になるデータストアからオブジェクトを選択するために使用するプロパティに複合インデックスを追加し、原則的に、などのアプリケーション高速化全体を-しかし、我々が把握する必要があると思いますどのプロパティがインデックス付けを必要としていました。アプリケーションのコードを簡単に見には、任意の明白な容疑者を明らかにしませんでした。

It was time to pull out the heavy machinery in our toolkit: using Dapper [Sig10], we traced the steps individual HTTP requests took—from their receipt by a frontend reverse proxy through to the point where the app’s code returned a response—and looked at the RPCs issued by each server involved in handling that request. Doing so would allow us to see which properties were included in requests to the datastore, then create the appropriate indices.

それは私たちのツールキットで重機を引き出すための時間だった：Dapperの使用して[SIG10]を、私たちは、個々のHTTP要求がかかった-からそのレシートフロントエンドのリバースプロキシによってアプリのコードが応答を-、返さ見えた時点までの手順をトレースその要求の処理に関与する各サーバーによって発行されたRPCで。そうすることで、適切なインデックスを作成して、私たちは、データストアへのリクエストに含まれたプロパティを参照することができるようになります。

While investigating, we discovered that requests for static content such as images, which weren’t served from the datastore, were also much slower than expected. Looking at graphs with file-level granularity, we saw their responses had been much faster only a few days before. This implied that the observed correlation between merge_join and the latency increase was spurious and that our suboptimal-indexing theory was fatally flawed.

調査が、我々はそのようなデータストアから提供されていなかった画像などの静的コンテンツに対する要求は、また、予想よりはるかに遅かったことを発見しました。ファイルレベルの粒度でのグラフを見ると、我々は彼らの応答があったはるかに速く、わずか数日前に見ました。これは、との間の観察された相関することを暗示 merge_joinし、待ち時間の増加が偽だったと私たちの次善のインデクシング理論は致命的な欠陥があったこと。

Examining the unexpectedly slow requests for static content, most of the RPCs sent from the application were to a memcache service, so the requests should have been very fast—on the order of a few milliseconds. These requests did turn out to be very fast, so the problem didn’t seem to originate there. However, between the time the app started working on a request and when it made the first RPCs, there was about a 250 ms period where the app was doing…well, something. Because App Engine runs code provided by users, its SRE team does not profile or inspect app code, so we couldn’t tell what the app was doing in that interval; similarly, Dapper couldn’t help track down what was going on since it can only trace RPC calls, and none were made during that period.

静的コンテンツの予想外に遅い要求を調べる、アプリケーションから送信されたRPCのほとんどはmemcacheのサービスにあったので、要求が非常に高速なオン数ミリ秒のオーダーされている必要があります。これらの要求は非常に高速であることが判明なかったので、問題はそこに由来していないようでした。しかし、時間の間にアプリがリクエストに応じて作業を開始し、それが最初のRPCを作ったとき、アプリは、...よくやっていた250ミリ秒の期間についてのあった 何かを。App Engineは、ユーザーによって提供されたコードを実行されるため、そのSREチームは、プロファイルやアプリケーションのコードを検査し、私たちは、アプリがその間隔で何をやっていた伝えることができなかったしません。同様に、Dapperのは、それが唯一のRPC呼び出しをトレースすることができますので、何が起こっていたか追跡助けることができなかった、とどれもその期間中に行われませんでした。

Faced with what was, by this point, quite a mystery, we decided not to solve it…yet. The customer had a public launch scheduled for the following week, and we weren’t sure how soon we’d be able to identify the problem and fix it. Instead, we recommended that the customer increase the resources allocated to their app to the most CPU-rich instance type available. Doing so reduced the app’s latency to acceptable levels, though not as low as we’d prefer. We concluded that the latency mitigation was good enough that the team could conduct their launch successfully, then investigate at leisure.18

何であったかに直面して、この点、非常に神秘によって、私たちは...それを解決しないことに決めたまだ。顧客は、次の週に予定一般公開があったが、我々は問題を特定し、それを修正することができるだろうかすぐにわかりませんでした。その代わりに、我々は、顧客が利用可能な最もCPUの豊富なインスタンスタイプに自分のアプリに割り当てられたリソースを増やすことをお勧め。我々が好むほど低くないのにそうすることで、許容可能なレベルまで、アプリの待ち時間を減少させました。私たちは、待ち時間の軽減はチームがレジャーで調査した後、正常に彼らの立ち上げを行うことができることを十分に良好であったと結論付けた。18

At this point, we suspected that the app was a victim of yet another common cause of sudden increases in latency and resource usage: a change in the type of work. We’d seen an increase in writes to the datastore from the app, just before its latency increased, but because this increase wasn’t very large—nor was it sustained—we’d written it off as coincidental. However, this behavior did resemble a common pattern: an instance of the app is initialized by reading objects from the datastore, then storing them in the instance’s memory. By doing so, the instance avoids reading rarely changing configuration from the datastore on each request, and instead checks the in-memory objects. Then, the time it takes to handle requests will often scale with the amount of configuration data.19 We couldn’t prove that this behavior was the root of the problem, but it’s a common antipattern.

仕事の種類の変更：この時点で、私たちは、アプリがレイテンシおよびリソースの使用状況の急激な増加のさらに別の一般的な原因の犠牲者だったことを疑いました。私たちは、その待ち時間が増加する直前に、アプリからのデータストアへの書き込みの増加を見たのだが、この増加は非常に大でもありませんでしただったので、それはwe'd-持続として偶然それをオフに書かれました。アプリのインスタンスは、インスタンスのメモリに格納し、その後、データストアからオブジェクトを読み取ることによって初期化されます。ただし、この動作は一般的なパターンに似ていました。そうすることによって、インスタンスはほとんど読んでいないことを回避する要求ごとにデータストアから設定を変更し、代わりに、メモリ内のオブジェクトをチェックします。そして、それは要求を処理するのにかかる時間は、多くの場合、コンフィギュレーション・データの量に比例します。19私たちは、この動作は問題の根本的だったことを証明することができませんでしたが、それは一般的なアンチパターンです。

The app developers added instrumentation to understand where the app was spending its time. They identified a method that was called on every request, that checked whether a user had whitelisted access to a given path. The method used a caching layer that sought to minimize accesses to both the datastore and the memcache service, by holding whitelist objects in instances’ memory. As one of the app’s developers noted in the investigation, “I don’t know where the fire is yet, but I’m blinded by smoke coming from this whitelist cache.”

アプリ開発者はアプリは、その時間を費やしたかを理解するためにインスツルメンテーションを追加しました。彼らは、ユーザーが指定したパスへのアクセスをホワイトリストに登録したかどうかをチェックし、すべての要求で呼び出されたメソッドを、同定しました。この方法は、各インスタンスのメモリ内のホワイトリストのオブジェクトを保持することによって、データストアやmemcacheのサービスの両方へのアクセスを最小限にしようとしたキャッシュ層を使用します。アプリの開発者の一人が捜査に述べたように、「火はまだここで私は知らないが、私は、このホワイトリストキャッシュから来る煙に目がくらみました。」

Some time later, the root cause was found: due to a long-standing bug in the app’s access control system, whenever one specific path was accessed, a whitelist object would be created and stored in the datastore. In the run-up to launch, an automated security scanner had been testing the app for vulnerabilities, and as a side effect, its scan produced thousands of whitelist objects over the course of half an hour. These superfluous whitelist objects then had to be checked on every request to the app, which led to pathologically slow responses—without causing any RPC calls from the app to other services. Fixing the bug and removing those objects returned the app’s performance to expected levels.

しばらくして、根本的な原因が見つかりました：原因でアプリケーションのアクセス制御システムにおける長年のバグのため、一つの特定のパスにアクセスしたときはいつでも、ホワイトリストオブジェクトが作成され、データストアに格納されます。起動するランアップでは、自動化されたセキュリティスキャナは、脆弱性のためのアプリケーションをテストしていた、と副作用として、そのスキャンは、半時間かけてホワイトリストオブジェクトの数千を生成しました。これらの余分なホワイトリストオブジェクトは、その後、病理学的に遅い応答-なしで他のサービスにアプリから任意のRPCコールを起こすに至ったアプリへのリクエストごとにチェックしなければなりませんでした。バグを修正し、それらのオブジェクトを削除すると予想されるレベルまで、アプリのパフォーマンスを返しました。

Making Troubleshooting Easier

トラブルシューティングが容易作ります

There are many ways to simplify and speed troubleshooting. Perhaps the most fundamental are:

トラブルシューティングを簡素化し、高速化するために多くの方法があります。おそらく、最も基本的なもの：

* Building observability—with both white-box metrics and structured logs—into each component from the ground up.

建物の観測-とホワイトボックスの指標と構造化されたログ-に地面からの各成分の両方。

* Designing systems with well-understood and observable interfaces between components.

間の十分に理解し、観察可能なインタフェースを備えたシステムを設計するコンポーネント。

Ensuring that information is available in a consistent way throughout a system—for instance, using a unique request identifier throughout the span of RPCs generated by various components—reduces the need to figure out which log entry on an upstream component matches a log entry on a downstream component, speeding the time to diagnosis and recovery.

様々なによって生成されたRPCのスパン全体でユニークな要求識別子を使用して、情報は、システムのインスタンス全体で一貫した方法で利用可能であることを確実にすることを把握する必要性コンポーネント-減少し、上流コンポーネントのエントリをログを上のログエントリと一致します下流成分、診断および回復までの時間を高速化。

Problems in correctly representing the state of reality in a code change or an environment change often lead to a need to troubleshoot. Simplifying, controlling, and logging such changes can reduce the need for troubleshooting, and make it easier when it happens.

正しくコード変更や環境の変化に現実の状態を表すの問題点は、多くの場合、トラブルシューティングの必要性につながります。、簡素化の制御、およびそのような変更をログに記録することは、トラブルシューティングの必要性を低減し、それが起こるとき、それが容易になります。

Conclusion

結論

We’ve looked at some steps you can take to make the troubleshooting process clear and understandable to novices, so that they, too, can become effective at solving problems. Adopting a systematic approach to troubleshooting—as opposed to relying on luck or experience—can help bound your services’ time to recovery, leading to a better experience for your users.

私たちは、あなたが、彼らは、あまりにも、問題を解決するのに有効になることができるように、初心者にトラブルシューティングのプロセスが明確で分かりやすくするために取ることができるいくつかのステップを見てきました。運かに依存するとは対照的に、トラブルシューティング-への体系的なアプローチを採用し、ユーザーのためのより良い経験につながる、回復へのサービスの時間を拘束さ役立つ経験することは、できます。
