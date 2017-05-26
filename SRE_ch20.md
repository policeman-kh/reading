# Load Balancing Policies

> Now that we’ve established the groundwork for how a given client task maintains a set of connections that are known to be healthy, let’s examine load balancing policies.

クライアントタスクが健康だといわれてるコネクションの集合を維持するか。の基礎が確立しましたので、ロードバランシングポリシーを検討してみましょう。

> These are the mechanisms used by client tasks to select which backend task in its subset receives a client request.

このポリシーは、クライアントのリクエストを受けるバックエンドタスクのサブセットを選択するために、
クライアントタスクによって使用されるメカニズムです。

> Many of the complexities in load balancing policies stem from the distributed nature of the decision-making process in which clients need to decide, in real time (and with only partial and/or stale backend state information), which backend should be used for each request.

ロードバランシングポリシーの複雑さの多くは、クライアントの意思決定プロセスにおける分散環境の特質が起因しています

意思決定プロセスとは...  どのバックエンドにリクエストするかで、クライアントはリアルタイムに、
部分的に、古くなったバックエンドの状態の情報を用いて、決定する必要がある

> Load balancing policies can be very simple and not take into account any information about the state of the backends (e.g., Round Robin) or can act with more information about the backends (e.g., Least-Loaded Round Robin or Weighted Round Robin).

ロードバランシングポリシーには、
　非常にシンプルで、バックエンドの状態を考慮しない、ラウンドロビン　や
　バックエンドのより多くの情報を用いても動作するLeast-Loaded、最小負荷ラウンドロビン や
  Weightedラウンドロビン　があります

## Simple Round Robin

> One very simple approach to load balancing has each client send requests in round-robin fashion to each backend task in its subset to which it can successfully connect and which isn’t in lame duck state.

ロードバランシングのシンプルなアプローチは、各クライアントが
レームダックな状態でない、正常に接続できるサブセット内のバックエンドのリクエストを送信する
ラウンドロビン方式です。

> For many years, this was our most common approach, and it’s still used by many services.

長年にわたり、これは最も一般的なアプローチで、多くのサービスで引き続き使用されています。

> Unfortunately, while Round Robin has the advantage of being very simple and performing significantly better than just selecting backend tasks randomly, the results of this policy can be very poor.

残念ながら、ラウンドロビンは、バックエンドタスクをランダムに選択するよりも、
シンプルで優れたパフォーマンスを発揮するというメリットを持ってますが、このポリシーの結果は非常にpoor、不十分です。

> While actual numbers depend on many factors, such as varying query cost and machine diversity, we’ve found that Round Robin can result in a spread of up to 2x in CPU consumption from the least to the most loaded task.

実際の数値は、クエリのコストやマシンの多様性のような多くの要因に依存しますが、
ラウンドロビンを使用すると、CPUの使用量において、負荷が最小のタスクと最大のタスクの差が 2x、2倍にまで広がることがわかりました。

> Such a spread is extremely wasteful and occurs for a number of reasons, including:

このような広がりは非常に無駄であり、以下の理由で発生します。

* Small subsetting

小さいsubsetting

* Varying query costs

クエリコストの変化

* Machine diversity

マシンの多様性

* Unpredictable performance factors

予測できないパフォーマンス要因

### SMALL SUBSETTING

> One of the simplest reasons Round Robin distributes load poorly is that all of its clients may not issue requests at the same rate.

Round Robinが負荷を不十分に分配する、最もシンプルな理由の1つは、すべてのクライアントが同じrate、速度でリクエストを発行できないことです。

> Different rates of requests among clients are especially likely when vastly different processes share the same backends.

異なるプロセスが同じバックエンドを共有する場合、クライアントのリクエストのrate差は特に大きくなります。

> In this case, and especially if you’re using relatively small subset sizes, backends in the subsets of the clients generating the most traffic will naturally tend to be more loaded.

この場合、特に、比較的小さなサブセットサイズを使用している場合は、トラフィックを最も多く生成するクライアントのバックエンドのサブセットは当然、より負荷がかかる傾向があります。

### VARYING QUERY COSTS - クエリコストの変化

> Many services handle requests that require vastly different amounts of resources for processing.

多くのサービスは、膨大で異なるリソース量のリクエストをハンドルしています。

> In practice, we’ve found that the semantics of many services in Google are such that the most expensive requests consume 1000x (or more) CPU than the cheapest requests.

実際に、Googleの多くのサービスのセマンティクスは、最もexpensive、高負荷なリクエストは
最もcheapest、低負荷なリクエストの1000x、1000倍（またはそれ以上）のCPUを消費することがわかりました。

> Load balancing using Round Robin is even more difficult when query cost can’t be predicted in advance.

クエリのコストを事前に予測できない場合、ラウンドロビンを使用したロードバランシングはさらに困難です

> For example, a query such as “return all emails received by user XYZ in the last day” could be very cheap (if the user has received little email over the course of the day) or extremely expensive.

たとえば、「最終日にユーザーXYZが受け取ったすべてのemailを返す」のようなクエリは、(ユーザーが1日のうちにほとんどメールを受信しなかった場合は)非常に低負荷ですが、(その逆、大量にメールを受診している場合は)きわめて高負荷です。

--

> Load balancing in a system with large discrepancies in potential query cost is very problematic.

潜在的なクエリコストが大きく矛盾するシステムのロードバランシングは非常に問題です。

> It can become necessary to adjust the service interfaces to functionally cap the amount of work done per request.

リクエストごとに実行される作業量を、機能的に制限するためには、サービスインターフェイスを調整する必要があります。

> For example, in the case of the email query described previously, you could introduce a pagination interface and change the semantics of the request to “return the most recent 100 emails (or fewer) received by user XYZ in the last day.”

たとえば、先ほど説明したemailクエリの場合、ページネーションのインターフェイスを導入し、
リクエストのセマンティクスを「最終日にユーザーXYZが受信した最新の100件のemailを返す」に変更します。

> Unfortunately, it’s often difficult to introduce such semantic changes.

しかし、残念なことに、このようなセマンティクスの変更を導入することは難しいことが多いです

> Not only does this require changes in all the client code, but it also entails additional consistency considerations.

この変更のために、すべてのクライアントコードの変更だけでなく、追加で、一貫性の考慮も必要とします

> For example, the user may be receiving new emails or deleting emails as the client fetches emails page-by-page.

たとえば、クライアントがページごとにemailをフェッチするときに、新しいemailを受信していたり、削除しているかもしれません

> For this use case, a client that naively iterates through the results and concatenates the responses (rather than paginating based on a fixed view of the data) will likely produce an inconsistent view, repeating some messages and/or skipping others.

このユースケースでは、一貫性のないビューを生成しまう可能性があります。

一貫性のないビューとは... ページネーションにおいて、いくつか同じemailを異なるページに表示したり、特定のメールをぺージネーション時にスキップしたり

--

> To keep interfaces (and their implementations) simple, services are often defined to allow the most expensive requests to consume 100, 1000, or even 10000 times more resources than the cheapest requests.

インターフェイス、およびその実装をシンプルに保つために、
最も高負荷なリクエストが最も低負荷なリクエストよりも100、1000、または10000 times・倍?
リソースを消費すると定義されることがよくあります。

> However, varying resource requirements per-request naturally mean that some backend tasks will be unlucky and occasionally receive more expensive requests than others.

しかし、リクエストごとにリソースの要件が変化することは、
当然、いくつかのバックエンドタスクがunlucky、不運になり、
場合によっては他よりも高負荷のリクエストを受け取ることがあります。

> The extent to which this situation affects load balancing depends on how expensive the most expensive requests are.

この状況がロードバランシングに影響を与える程度は、最も高負荷なリクエストがどのくらい高負荷であるかによります。

> For example, for one of our Java backends, queries consume around 15 ms of CPU on average but some queries can easily require up to 10 seconds.

たとえば、私たちのあるJavaバックエンドタスクでは、クエリの平均CPU使用時間は約15ミリ秒ですが、いくつかクエリでは最大10秒かかることがあります。

> Each task in this backend reserves multiple CPU cores, which reduces latency by allowing some of the computations to happen in parallel.

このバックエンドの各タスクは複数のCPUコアを予約し、計算のいくつかをパラレルで実行することで、レイテンシを短縮します。

> But despite these reserved cores, when a backend receives one of these large queries, its load increases significantly for a few seconds.

しかし、このような予約済みコアにもかかわらず、バックエンドが大きなクエリの1つを受け取ると、その負荷は数秒間、大きく増加します。

> A poorly behaved task may run out of memory or even stop responding entirely (e.g., due to memory thrashing), but even in the normal case (i.e., the backend has sufficient resources and its load normalizes once the large query completes), the latency of other requests suffers due to resource competition with the expensive request.

poorly behaved task、十分にリソースが確保されていないタスクは、メモリが不足したり、
メモリスラッシングで、完全に応答が停止することもあります

十分なリソースが確保されている、通常のケースでも
高負荷なリクエストがあると、他のリクエストのレイテンシはリソースの競合のため、苦しんでいます

### MACHINE DIVERSITY - マシンの多様性

> Another challenge to Simple Round Robin is the fact that not all machines in the same datacenter are necessarily the same.

シンプルなラウンドロビンのもう一つの課題は、同じデータセンターのすべてのマシンが、必ずしも同じ性能ではない ということです。

> A given datacenter may have machines with CPUs of varying performance, and therefore, the same request may represent a significantly different amount of work for different machines.

データセンターには、さまざまなパフォーマンスのCPUを搭載したマシンが存在する可能性があり、
それゆえ、同じリクエストでも、マシンの性能差のために、作業量が大きく異なる可能性があります。

--

> Dealing with machine diversity—without requiring strict homogeneity—was a challenge for many years at Google.

厳密に同じであることを必要としない、マシンの多様性を扱うことは、Googleの長年の課題でした。

> In theory, the solution to working with heterogeneous resource capacity in a fleet is simple:

理論的には、fleet、フリート内の不均一なリソースキャパシティを用いて作業するソリューションは簡単です。

> scale the CPU reservations depending on the processor/machine type.

(どうするかというと.. )プロセッサ、マシンのタイプに応じて、CPUのreservation、予約をスケーリングします。

> However, in practice, rolling out this solution required significant effort because it required our job scheduler to account for resource equivalencies based on average machine performance across a sampling of services.

しかし、実際には、このソリューションを展開するには大きな労力が必要でした

ジョブスケジューラが、サンプリングサービス全体の平均なマシンパフォーマンスに基づいて、リソースの等価性を把握する必要があるため。です。

> For example, 2 CPU units in machine X (a “slow” machine) is equivalent to 0.8 CPU units in machine Y (a “fast” machine).

たとえば、マシンX(低速なマシン)の 2 CPUユニットは、マシンY(高速なマシン)の 0.8 CPUユニットに相当します。

> With this information, the job scheduler is then required to adjust CPU reservations for a process based upon the equivalence factor and the type of machine on which the process was scheduled.

この情報を用いて、ジョブスケジューラは、等価係数とマシンのタイプに基づいて、プロセスのCPU予約数を調整する必要があります。

> In an attempt to mitigate this complexity, we created a virtual unit for CPU rate called “GCU” (Google Compute Units).

この複雑さを緩和しようと、「GCU」（Google Compute Units）と呼ばれるCPU rateのための仮想ユニットを作成しました。

> GCUs became the standard for modeling CPU rates, and were used to maintain a mapping from each CPU architecture in our datacenters to its corresponding GCU based upon its performance.

GCUはCPU rateの標準的なモデリングとなり、そして
私たちのデータセンターの各CPUアーキテクチャから、そのパフォーマンスに対応するGCUにマッピングされています。

### UNPREDICTABLE PERFORMANCE FACTORS - 予測できないパフォーマンス要因

> Perhaps the largest complicating factor for Simple Round Robin is that machines—or, more accurately, the performance of backend tasks—may differ vastly due to several unpredictable aspects that cannot be accounted for statically.

おそらく、シンプルなラウンドロビンの最も複雑な要因は
statically、静的に説明できない、幾つかの予測不可能なaspects、側面？のために
バックエンドタスクのパフォーマンスが大きく異なることです。

> Two of the many unpredictable factors that contribute to performance include:

パフォーマンスに貢献する多くの予測不可能な要素のうち、2つは次のとおりです。

#### Antagonistic neighbors -- 敵対する隣人

> Other processes (often completely unrelated and run by different teams) can have a significant impact on the performance of your processes.

他のプロセス、完全に無関係で、異なるチームによって実行されるプロセスは、
あなたのプロセスのパフォーマンスに重大な影響を及ぼす可能性があります。

> We’ve seen differences in performance of this nature of up to 20%.

私たちは、最大20%のこの性質による性能差を見てきました

> This difference mostly stems from competition for shared resources, such as space in memory caches or bandwidth, in ways that may not be directly obvious.

この差の大部分は、メモリキャッシュや帯域幅のような、直接明らかにならない共有リソースの競合が起因しています

> For example, if the latency of outgoing requests from a backend task grows (because of competition for network resources with an antagonistic neighbor), the number of active requests will also grow, which may trigger increased garbage collection.

たとえば、ネットワークリソース競合のために、バックエンドタスクからのリクエスト発信のレイテンシが長くなると、
アクティブなリクエスト数も増加し、ガベージコレクションが増加するトリガーになるかもしれません。

#### Task restarts -- タスクのrestart

> When a task gets restarted, it often requires significantly more resources for a few minutes.

タスクが再起動されると、数分間、かなり多くのリソースが必要になります。

> As just one example, we’ve seen this condition affect platforms such as Java that optimize code dynamically more than others.

ほんの一例として、この状態が、コードを動的に最適化するJavaのようなプラットフォームに影響することを見たことがあります。

> In response, we’ve actually added to the logic of some server code — we keep servers in lame duck state and prewarm them (triggering these optimizations) for a period of time after they start, until their performance is nominal.

これに対応して、私たちは、いくつかのサーバーコードのロジックを実際に追加しました。

私たちは、サーバ起動後一定期間、サーバの性能が基準に達するまで、
レームダック状態と、最適化を引き起こすpre-warmを保持します

> The effect of task restarts can become a sizable problem when you consider we update many servers (e.g., push new builds, which requires restarting these tasks) every day.

毎日、多くのサーバーを更新が発生し、そのうちrestartを必要とするpushも含まれることを考慮すると
このタスクの再起動の影響は大きな問題になります。

> If your load balancing policy can’t adapt to unforeseen performance limitations, you will inherently end up with a suboptimal load distribution when working at scale.

あなたのロードバランシングポリシーが不測のパフォーマンスの制限に適応できない、
かつスケールで動作するときは、
suboptimal、準最適な、つまり最適ではない負荷分散を本質的に用いることになるでしょう

## Least-Loaded Round Robin - 最小負荷のラウンドロビン

> An alternative approach to Simple Round Robin is to have each client task keep track of the number of active requests it has to each backend task in its subset and use Round Robin among the set of tasks with a minimal number of active requests.

シンプルなラウンドロビンの代わりとなるアプローチは、
クライアントタスクがバックエンドタスクへのアクティブなリクエスト数をサブセット毎に記録し、保持し、
一連のタスクの中で、アクティブなリクエストの最小数を、ラウンドロビンの条件をして使用します

> For example, suppose a client uses a subset of backend tasks t0 to t9, and currently has the following number of active requests against each backend:

たとえば、クライアントがバックエンドタスク t0〜t9のサブセットを使用し、
現在、各バックエンドに対して、次の図のようなアクティブリクエスト数を保有しているとします。


| t0 | t1 | t2 | t3 | t4 | t5 | t6 | t7 | t8 | t9 |
| -- | -- | -- | -- | -- | -- | -- | -- | -- | -- |
|  2 |  1 |  0 |  0 |  1 |  0 |  2 |  0 |  0 |  1 |


> For a new request, the client would filter the list of potential backend tasks to just those tasks with the least number of connections (t2, t3, t5, t7, and t8) and choose a backend from that list.

新しいリクエストが発生すると、
バックエンドタスクのリストから、最小接続数（t2、t3、t5、t7、およびt8）のタスクをフィルタリングし
その中からバックエンドを選択します。

> Let’s assume it picks t2.

ここでは、t2を選ぶと仮定します

> The client’s connection state table would now look like the following:

クライアントの接続状態のテーブルは次のようになります。

| t0 | t1 | t2 | t3 | t4 | t5 | t6 | t7 | t8 | t9 |
| -- | -- | -- | -- | -- | -- | -- | -- | -- | -- |
|  2 |  1 |  1 |  0 |  1 |  0 |  2 |  0 |  0 |  1 |

> Assuming none of the current requests have completed, on the next request, the backend candidate pool becomes t3, t5, t7, and t8.

現在のリクエストが完了していないと仮定すると、次の新しいリクエストで、バックエンドの候補はt3、t5、t7、およびt8になります。

> Let’s fast-forward until we’ve issued four new requests.

4つの新しいリクエストを発行するまで、早送りしましょう。

> Still assuming that no request finishes in the meantime, the connection state table would look like the following:

この間にリクエストが終了しないと仮定すると、接続状態のテーブルは次のようになります。

| t0 | t1 | t2 | t3 | t4 | t5 | t6 | t7 | t8 | t9 |
| -- | -- | -- | -- | -- | -- | -- | -- | -- | -- |
|  2 |  1 |  1 |  1 |  1 |  1 |  2 |  1 |  1 |  1 |

> At this point the set of backend candidates is all tasks except t0 and t6.

この時点で、次のバックエンドの候補は、t0およびt6以外のすべてのタスク　になります。

> However, if the request against task t4 finishes, its current state becomes “0 active requests” and a new request will be assigned to t4.

しかし、タスクt4に対するリクエストが終了すると、t4の現在の状態は「0 アクティブリクエスト」になり、
新たなリクエストはt4に割り当てられるでしょう。

> This implementation actually uses Round Robin, but it’s applied across the set of tasks with minimal active requests.

この実装では、実際にはラウンドロビンが使用されますが、最小限のアクティブなリクエストを一連のタスクに適用します。

> Without such filtering, the policy might not be able to spread the requests well enough to avoid a situation in which some portion of the available backend tasks goes unused.

このようなフィルタリングがなければ、
利用可能なバックエンドタスクの一部が使用されない状況を回避するために、リクエストを十分に広げることができないかもしれません。

> The idea behind the least-loaded policy is that loaded tasks will tend to have higher latency than those with spare capacity, and this strategy will naturally take load away from these loaded tasks.

least-loadedポリシーの背後にあるアイデアは、
ロードされたタスクは余裕のあるタスクよりもレイテンシが長くなる傾向があり、
この戦略はロードされたタスクから自然に負荷を奪うことになります。

--

> All that said, we’ve learned (the hard way!) about one very dangerous pitfall of the Least-Loaded Round Robin approach:

一連のことを通して、私たちは、Least-Loadedラウンドロビンのアプローチの、非常に危険な落とし穴について学びました

> if a task is seriously unhealthy, it might start serving 100% errors.

タスクがひどくunhealthyであれば、100％エラーになる可能性があります。

> Depending on the nature of those errors, they may have very low latency;

エラーの性質によっては、レイテンシが非常に短くなることがあります。

> it’s frequently significantly faster to just return an “I’m unhealthy!” error than to actually process a request.

実際にリクエストを処理するよりも、エラー"I’m unhealthy!"を返した時の方が、ずっと速いことがあります。

> As a result, clients might start sending a very large amount of traffic to the unhealthy task, erroneously thinking that the task is available, as opposed to fast-failing them!

その結果、クライアントはunhealthyなタスクに非常に大量のトラフィックを送信し始め、
クライアントはそのタスクは高速に失敗しているのではなく、利用可能である. と誤って認識してしまう可能性があります。

> We say that the unhealthy task is now sinkholing traffic.

私たちは、unhealthyなタスクは、交通におけるシンクホール、(陥没している道路) である。と言います。

> Fortunately, this pitfall can be solved relatively easily by modifying the policy to count recent errors as if they were active requests.

幸いにも、この落とし穴は、アクティブなリクエストであれば、最近のエラーをカウントするよう
ポリシーを変更することで、比較的簡単に解決できます。

> This way, if a backend task becomes unhealthy, the load balancing policy begins to divert load from it the same way it would divert load from an overburdened task.

このように、バックエンドタスクがunhealthyになると、ロードバランシングポリシーは、
過負荷状態のタスクから負荷を迂回させるのと同じ方法で、負荷の迂回を開始します。

--

> Least-Loaded Round Robin has two important limitations:

Least-Loadedラウンドロビンには、2つの重要な制限があります。

- The count of active requests may not be a very good proxy for the capability of a given backend

アクティブなリクエストの数は、与えられたバックエンドのキャパビリティのために、良いプロキシでないかもしれません

> Many requests spend a significant portion of their life just waiting for a response from the network (i.e., waiting for responses to requests they initiate to other backends) and very little time on actual processing.

多くのリクエストは、ネットワークからのレスポンスを待っていることに、life、生涯の大部分を費やしています
すなわち、他のバックエンドへのリクエストを開始するために、レスポンスを待っています

そして、実際に処理する時間はとても短いです

> For example, one backend task may be able to process twice as many requests as another (e.g., because it’s running in a machine with a CPU that’s twice as fast as the rest), but the latency of its requests may still be roughly the same as the latency of requests in the other task (because requests spend most of their life just waiting for the network to respond).

例えば、ある1つのバックエンドタスクは、2倍のCPUが搭載されたマシンで実行されているため、他の2倍のリクエストを処理することができます

しかし、このリクエストのレイテンシは、他のタスクのリクエストのレイテンシとほぼ同じである場合があります
このリクエストの生涯のほとんどが、ネットワークが応答するのを待っているだけであったためです

> In this case, because blocking on I/O often consumes zero CPU, very little RAM, and no bandwidth, we’d still want to send twice as many requests to the faster backend.

この場合、I/Oでブロックされていると、CPU使用率は0になり、RAMの使用率は少なく、ネットワーク帯域幅もありません。
そして私たちは高速なバックエンドに2倍のリクエストを送信したいと考えます。

> However, Least-Loaded Round Robin will consider both backend tasks equally loaded.

しかし、Least-Loadedラウンドロビンは、レイテンシが同じであるため、両方のバックエンドタスクが均等にロードされているとみなします。

- The count of active requests in each client doesn’t include requests from other clients to the same backends

クライアントのアクティブなリクエスト数には、同じバックエンド、かつ他のクライアントからのリクエストは含まれません

> That is, each client task has only a very limited view into the state of its backend tasks: the view of its own requests.

つまり、各クライアント・タスクは、バックエンド・タスクの状態の、非常に限定されたビューしか持っていません。

> In practice, we’ve found that large services using Least-Loaded Round Robin will see their most loaded backend task using twice as much CPU as the least loaded, performing about as poorly as Round Robin.

実際には、Least-Loadedラウンドロビンを使用する大規模なサービスでは
負荷の高いバックエンドタスクが、負荷の低いタスクの2倍CPUを使用する傾向が見られ、
結局、ラウンドロビンと同じくらい劣っています

### Weighted Round Robin

> Weighted Round Robin is an important load balancing policy that improves on Simple and Least-Loaded Round Robin by incorporating backend-provided information into the decision process.

Weightedラウンドロビンは、バックエンドから提供される情報を決定プロセスに組み込むことで、
シンプルおよびLeast-Loaded ラウンドロビンを向上させる、重要なロードバランシングポリシーです。

> Weighted Round Robin is fairly simple in principle:

Weighted ラウンドロビンの原理はかなりシンプルです

> each client task keeps a “capability” score for each backend in its subset.

クライアントタスクは、そのサブセット内のバックエンドについて、キャパビリティスコアを保持します

> Requests are distributed in Round-Robin fashion, but clients weigh the distributions of requests to backends proportionally.

リクエストはラウンドロビン方式で配信されますが、クライアントは　バックエンドへのリクエストの分布を重み付けします

> In each response (including responses to health checks), backends include the current observed rates of queries and errors per second, in addition to the utilization (typically, CPU usage).

バックエンドが提供する情報には、利用率（通常は、CPU使用率）に加えて、現在の観察されたクエリのrateとエラー率が含まれます。

> Clients adjust the capability scores periodically to pick backend tasks based upon their current number of successful requests handled and at what utilization cost;

クライアントは、現在の成功したリクエスト数と利用コストからバックエンドタスクを選択するため、capabilityスコアを定期的に調整します

> failed requests result in a penalty that affects future decisions.

失敗したリクエストは、将来の決定に影響するペナルティをもたらします

> In practice, Weighted Round Robin has worked very well and significantly reduced the difference between the most and the least utilized tasks.

実際に、Weightedラウンドロビンは非常にうまく機能し、使用頻度の高いタスクと使用頻度の低いタスクの差を大幅に縮小しました。

> Figure 20-6 shows the CPU rates for a random subset of backend tasks around the time its clients switched from Least-Loaded to Weighted Round Robin.

次の図は、クライアントがLeast-LoadedラウンドロビンからWeightedラウンドロビンに切り替えた前後の
バックエンドタスクのランダムサブセットのCPUレートを示しています。

> The spread from the least to the most loaded tasks decreased drastically.

最も負荷の低いタスクから、最も負荷の大きいタスクへの広がりは大幅に減少しました。
