# Chapter 21. Handling Overload

> Avoiding overload is a goal of load balancing policies.

overloadを避けることは、load balancing ポリシーのゴールの一つです。

> But no matter how efficient your load balancing policy, eventually some part of your system will become overloaded.

しかし、たとえどんなにあなたのload balancing ポリシーが効率的であっても、結局、システムのいくつかの部分はoverloadになるでしょう

> Gracefully handling overload conditions is fundamental to running a reliable serving system.

overloadの状態を適切にハンドリングすることは、信頼性の高いサービスを実行する上で基本的なことです。

> One option for handling overload is to serve degraded responses: responses that are not as accurate as or that contain less data than normal responses, but that are easier to compute. For example:

overloadをハンドリングする一つの選択肢は、劣化した、本来のレスポンスよりも低品質の？レスポンスを提供することです：
このレスポンスは、通常よりも正確ではなく、データ量も少ないが、計算することは簡単です。
例えば、

* Instead of searching an entire corpus to provide the best available results to a search query, search only a small percentage of the candidate set.

検索クエリに有効な最良の結果を提供するよう、コーパス全体を検索する代わりに、候補となる集合のわずかの割合のみを検索します

* Rely on a local copy of results that may not be fully up to date but that will be cheaper to use than going against the canonical storage.

完全に最新でないかもしれない結果のローカルコピーを使用にします。
これは標準的なストレージに比べて、使用するのがとても安価？低負荷になるでしょう。

> However, under extreme overload, the service might not even be able to compute and serve degraded responses.

しかし、極端なoverload配下の場合、サービスはこの劣化したレスポンスを計算して、提供することさえできないかもしれません

> At this point it may have no immediate option but to serve errors.

この時点で、選択肢はなくエラーを提供することになるかもしれません

> One way to mitigate this scenario is to balance traffic across datacenters such that no datacenter receives more traffic than it has the capacity to process.

このシナリオを緩和する1つの方法は、データセンターが処理能力以上のトラフィックを受信することがないよう、データセンター間のトラフィックのバランスをとることです。

> For example, if a datacenter runs 100 backend tasks and each task can process up to 500 requests per second, the load balancing algorithm will not allow more than 50,000 queries per second to be sent to that datacenter.

たとえば、あるデータセンターで100個のバックエンドタスクが実行され、各タスクが1秒あたり500個のリクエストを処理できる場合、
このload balancingアルゴリズムでは、データセンターに毎秒50,000件以上、クエリされることは許可されないでしょう。

> However, even this constraint can prove insufficient to avoid overload when you’re operating at scale.

しかし大規模な操作の場合は、この制約でさえも、このoverloadを回避するために、不十分と証明することができます。

> At the end of the day, it’s best to build clients and backends to handle resource restrictions gracefully:

結局のところ、リソースの制限を適切に処理するために、クライアントとバックエンドを構築することがベストです。

> redirect when possible, serve degraded results when necessary, and handle resource errors transparently when all else fails.

(その方法は)可能であればリダイレクトし、必要であれば劣化した結果を提供し、それでもだめなときはリソースのエラーを透過的にハンドリングします。

## The Pitfalls of “Queries per Second” - 秒あたりのクエリ数の落とし穴

> Different queries can have vastly different resource requirements.

異なるクエリは、リソース要件が大きく異なる可能性があります

> A query’s cost can vary based on arbitrary factors such as the code in the client that issues them (for services that have many different clients) or even the time of the day

クエリのコストは、それらを発行するクライアントのコード、あるいは時間帯のような、任意の要因に基づいて、変化する可能性があります

> (e.g., home users versus work users; or interactive end-user traffic versus batch traffic).

例えば、home users vs work users ?　あるいは 対話的なエンドユーザのトラフィック vs バッチトラフィック

> We learned this lesson the hard way:

私たちはこのレッスンを難しい方法で学びました ?

modeling capacity as “queries per second” or using static features of the requests that are believed to be a proxy for the resources they consume (e.g., “how many keys are the requests reading”) often makes for a poor metric.

「1秒あたりのクエリ数」としてのキャパシティをモデリングすること
リソースのプロキシと考えられるリクエストの静的な機能を使用すること（例えば、どのくらいのキーが読み取り中のリクエストであるか）
それらは、しばしばpoor、貧弱なmetric、測定基準を作り出します。

> Even if these metrics perform adequately at one point in time, the ratios can change.

たとえこれらのメトリックがある時点で適切に機能したとしても、比率は変更される可能性があります。

> Sometimes the change is gradual, but sometimes the change is drastic (e.g., a new version of the software suddenly made some features of some requests require significantly fewer resources).

この変更は徐々に行われることもありますが、激しい変更の場合もあります
（たとえば、ソフトウェアの新しいバージョンが、突然、いくつかのリクエストのいくつかの機能に大幅なリソースを必要とする。など）

> A moving target makes a poor metric for designing and implementing load balancing.

移動する目標は、load balancingを設計して実装するために、poor 貧弱なメトリックとなります。

--

> A better solution is to measure capacity directly in available resources.

よりベターな解決策は、利用可能なリソースで、直接、capacityを測定することです。

> For example, you may have a total of 500 CPU cores and 1 TB of memory reserved for a given service in a given datacenter.

たとえば、データセンター内のサービスに、あなたは合計500個のCPUコアと1TBのメモリを保有しているかもしれません

> Naturally, it works much better to use those numbers directly to model a datacenter’s capacity.

当然、データセンターのcapacityをモデル化するために、これらの数値を直接使用する方がずっと効果的です

> We often speak about the cost of a request to refer to a normalized measure of how much CPU time it has consumed (over different CPU architectures, with consideration of performance differences).

私たちはしばしば、CPU使用時間の標準化された指標を参照するために、リクエストのコストについて話します

パフォーマンスの違いを考慮して、異なるCPUアーキテクチャ上で

--

> In a majority of cases (although certainly not in all), we’ve found that simply using CPU consumption as the signal for provisioning works well, for the following reasons:

ほとんどの場合、全てではありませんが
プロビジョニングのためのシグナルとしてCPU使用量を使用するだけで、うまく動作することがわかりました
理由は次のとおりです

* In platforms with garbage collection, memory pressure naturally translates into increased CPU consumption.

ガーベジコレクションを使用するプラットフォームでは、メモリの圧迫が当然、CPU使用量の増加につながります。

* In other platforms, it’s possible to provision the remaining resources in such a way that they’re very unlikely to run out before CPU runs out.

他のプラットフォームでは、CPUが使い果たされる前に枯渇しないよう、残りのリソースを供給することは可能です

> In cases where over-provisioning the non-CPU resources is prohibitively expensive, we take each system resource into account separately when considering resource consumption.

CPU以外のリソースの過剰なプロビジョニングが非常に高価？高負荷な場合、リソース消費を考慮する時に、各システムリソースを個別に考慮します。

## Per-Customer Limits - customer一人あたりの制限

> One component of dealing with overload is deciding what to do in the case of global overload.

overloadを扱う1コンポーネントは、グローバルなoverloadの場合に、何をすべきかを決定しています

> In a perfect world, where teams coordinate their launches carefully with the owners of their backend dependencies, global overload never happens and backend services always have enough capacity to serve their customers.

チームがバックエンドの依存関係であるownerとともに、慎重にローンチを調整している完璧な世界では、
グローバルなoverloadは起こることはなく、バックエンドサービスは常に彼らのcustomerに対応できるだけのcapacityを備えています。

> Unfortunately, we don’t live in a perfect world.

残念ながら、我々は完璧な世界に住んでいません。

> Here in reality, global overload occurs quite frequently (especially for internal services that tend to have many clients run by many teams).

実際には、グローバルなoverloadは頻繁に発生します。

特に、多くのチームによって、多くのクライアントが運営される傾向のあるinternalなサービスの場合

--

> When global overload does occur, it’s vital that the service only delivers error responses to misbehaving customers, while other customers remain unaffected.

グローバルなoverloadが発生した時、他のcustomerは影響を及ぼさないで、サービスが誤動作しているcustomerにのみ、エラーをレスポンスすることが重要です。

> To achieve this outcome, service owners provision their capacity based on the negotiated usage with their customers and define per-customer quotas according to these agreements.

この結果を達成するために、サービスオーナーは、customerとの交渉された使用状況に基づいて、
capacityを供給し、契約に従ってcustomerごとのquota、割り当てを定義します。

> For example, if a backend service has 10,000 CPUs allocated worldwide (over various datacenters), their per-customer limits might look something like the following:

たとえば、バックエンドサービスに、様々なデータセンターを介して世界規模で、10000CPUが割り当てられている場合、customerあたりのlimitは次のようになります。

* Gmail is allowed to consume up to 4,000 CPU seconds per second.

Gmailは、1秒あたり4,000 CPU秒を消費することができる

* Calendar is allowed to consume up to 4,000 CPU seconds per second.

カレンダーは、1秒あたり4,000 CPU秒を消費することができる

* Android is allowed to consume up to 3,000 CPU seconds per second.

Androidは、1秒あたり3,000 CPU秒を消費することができる

* Google+ is allowed to consume up to 2,000 CPU seconds per second.

Google+は、1秒あたり2,000 CPU秒を消費することができる

* Every other user is allowed to consume up to 500 CPU seconds per second.

他のすべてのユーザーは、1秒あたり500 CPU秒を消費することができる

> Note that these numbers may add up to more than the 10,000 CPUs allocated to the backend service.

これらの数値の合計が、バックエンドサービスに割り当てられた10,000CPU以上になる可能性があることを書き留めてください

> The service owner is relying on the fact that it’s unlikely for all of their customers to hit their resource limits simultaneously.

サービスのownerは、すべてのcustomerが同時にリソース制限に達する可能性は低い. という事実を当てしています。

> We aggregate global usage information in real time from all backend tasks, and use that data to push effective limits to individual backend tasks.

私たちは、すべてのバックエンドタスクからグローバルな使用状況情報をリアルタイムで集計し、そのデータを使用して、個々のバックエンドタスクに有効なlimitを適用します

> A closer look at the system that implements this logic is outside of the scope of this discussion, but we’ve written significant code to implement this in our backend tasks.

このロジックを実装するシステムを詳しく見ていくことはこの議論の範囲外ですが、バックエンドタスクにこれを実装するための重要なコードを記述しています。

> An interesting part of the puzzle is computing in real time the amount of resources—specifically CPU—consumed by each individual request.

興味深い箇所は、個々のリクエストごとにCPU使用量をリアルタイムに計算するところです。

> This computation is particularly tricky for servers that don’t implement a thread-per-request model, where a pool of threads just executes different parts of all requests as they come in, using nonblocking APIs.

この計算は、nonblocking APIを使用して、スレッドプールがすべてのリクエストの異なるパートで実行する、
リクエストごとのスレッドモデルを実装していないサーバーでは、特に難しいことです。

## Client-Side Throttling - クライアントサイド スロットル

> When a customer is out of quota, a backend task should reject requests quickly with the expectation that returning a “customer is out of quota” error consumes significantly fewer resources than actually processing the request and serving back a correct response.

customerが"out of quota" (事前に割り当てられた容量を超えた時)、バックエンドタスクは、
実際にリクエストを処理して正しいレスポンスを返すことよりも
わずかなリソースしか消費しない “customer is out of quota”というレスポンスを返し、すばやく拒否すべきです

> However, this logic doesn’t hold true for all services.

ただし、このロジックはすべてのサービスに当てはまるわけではありません。

> For example, it’s almost equally expensive to reject a request that requires a simple RAM lookup (where the overhead of the request/response protocol handling is significantly larger than the overhead of producing the response) as it is to accept and run that request.

例えば、単純なRAMルックアップを必要とするリクエストを拒否することは、
その要求を受け入れて実行することと、ほぼ同じくらいのコストがかかります

リクエスト/レスポンスのプロトコルハンドリングのオーバーヘッドが、レスポンスを生成するオーバーヘッドよりもはるかに大きいので

> And even in the case where rejecting requests saves significant resources, those requests still consume some resources.

また、重要なリソースを保存するリクエストを拒否する場合も、それらのリクエストは依然として いくつかのリソースを消費します

> If the amount of rejected requests is significant, these numbers add up quickly.

拒否されたリクエストの量が著しい場合、これらの数値はすぐに合計されます。

> In such cases, the backend can become overloaded even though the vast majority of its CPU is spent just rejecting requests!

そのような場合、CPUの大部分がリクエストを拒否するだけであっても、バックエンドはoverloadになる可能性があります。

--

> Client-side throttling addresses this problem.

クライアントサイド スロットリングはこの問題を解決します。

> When a client detects that a significant portion of its recent requests have been rejected due to “out of quota” errors, it starts self-regulating and caps the amount of outgoing traffic it generates.

クライアントは、最近のリクエストの大部分が"out of quota" エラーのために拒否されたことを検出すると、
self-regulating、自己規制を開始し、送信トラフィックの作成量を制限します。

> Requests above the cap fail locally without even reaching the network.

制限中のリクエストは、ネットワークに到達せずに、ローカルで失敗します。

> We implemented client-side throttling through a technique we call adaptive throttling.

私たちは、クライアントサイド スロットリングをadaptiveスロットリングと呼ばれる手法で実装しました。

> Specifically, each client task keeps the following information for the last two minutes of its history:

具体的には、各クライアントタスクは、その履歴の最新の2分間、次の情報を保持します。

#### requests

> The number of requests attempted by the application layer (at the client, on top of the adaptive throttling system)

( クライアント、adaptiveスロットリングシステム上の )
アプリケーションレイヤーで、試行されたリクエスト数

#### accepts

> The number of requests accepted by the backend

バックエンドが受け入れたリクエスト数

> Under normal conditions, the two values are equal.

ノーマルな条件下では、2つの値は等しいです

> As the backend starts rejecting traffic, the number of accepts becomes smaller than the number of requests.

バックエンドがトラフィックをrejectし始めると、acceptsの数はrequestsの数よりも少なくなります。

> Clients can continue to issue requests to the backend until requests is K  times as large as accepts.

クライアントは、リクエストがacceptsのK倍に達するまで、バックエンドにリクエストを発行し続けることができます。

> Once that cutoff is reached, the client begins to self-regulate and new requests are rejected locally (i.e., at the client) with the probability calculated in Equation 21-1.

そのcutoffに達すると、クライアントは自己規制を開始し、次の式:21-1 で計算された確率で
新しいリクエストをローカル（つまりクライアントで）で拒否します。

Equation 21-1. Client request rejection probability

++++ 図 ++++

> As the client itself starts rejecting requests, requests will continue to exceed accepts.

クライアント自身がリクエストをrejectし始めると、(上の式の)"requests"は"accepts"を超えます。

> While it may seem counterintuitive, given that locally rejected requests aren’t actually propagated to the backend, this is the preferred behavior.

直感に反するように思えるかもしれませんが、ローカルでrejectされたリクエストは実際にバックエンドに伝播されないので、これは好ましい動作です

> As the rate at which the application attempts requests to the client grows (relative to the rate at which the backend accepts them), we want to increase the probability of dropping new requests.

バックエンドが受け入れる割合と比較して、リクエストを試みる割合が増加するにつれて
私たちは、新しいリクエストをdropする確率を高めたいと考えています。

> For services where the cost of processing a request is very close to the cost of rejecting that request, allowing roughly half of the backend resources to be consumed by rejected requests can be unacceptable.

リクエストを処理するコストと、リクエストをrejectするコストが非常に近いサービスで
バックエンドのリソースのおよそ半分が、rejectされたリクエストで消費されることは、許容することができません。

> In this case, the solution is simple:

この場合の、解決策はシンプルです。

> modify the accepts multiplier upper K (e.g., 2) in the client request rejection probability (Equation 21-1).

クライアントのリクエストをrejectする確率において、acceptsの乗数Kを修正します

> In this way:

この方法では

* Reducing the multiplier will make adaptive throttling behave more aggressively

乗数Kを減らすと、adaptiveスロットリングがよりアグレッシブに動作します

* Increasing the multiplier will make adaptive throttling behave less aggressively

乗数Kを増やすと、adaptiveスロットリングがアグレッシブに動作しなくなります

> For example, instead of having the client self-regulate when requests = 2 * accepts, have it self-regulate when requests = 1.1 * accepts.

たとえば、requests = 2 * acceptsのときにクライアントの自己規制を行わないで、
requests = 1.1 * acceptsのときに自己規制を用いるようにします。

> Reducing the modifier to 1.1 means only one request will be rejected by the backend for every 10 requests accepted.

乗数を1.1に減らすということは、acceptされた10リクエストごとに、
1リクエストだけがバックエンドによって、rejectされることを意味します。

--

> We generally prefer the 2x multiplier.

私たちは一般的に乗数 2x を好みます。

> By allowing more requests to reach the backend than are expected to actually be allowed, we waste more resources at the backend, but we also speed up the propagation of state from the backend to the clients.

実際に許可されるよりも、多くのリクエストをバックエンドに届けることで、
バックエンドではより多くのリソースを消費しますが、バックエンドからクライアントへの伝播もまたスピードアップします。

> For example, if the backend decides to stop rejecting traffic from the client tasks, the delay until all client tasks have detected this change in state is shorter.

たとえば、バックエンドがクライアントタスクから、rejectしているトラフィックを停止すると判断した場合、
すべてのクライアントタスクがこの状態の変化を検出するまでの遅延は、より短くなります。

> We’ve found adaptive throttling to work well in practice, leading to stable rates of requests overall.

私たちはadaptiveスロットリングが実際にうまく機能することがわかったので、安定したリクエストの割合が全体的に向上しました。

> Even in large overload situations, backends end up rejecting one request for each request they actually process.

大規模なoverloadな状態であっても、バックエンドは実際に処理するリクエストごとに、リクエストをrejectし、処理を終えます

> One large advantage of this approach is that the decision is made by the client task based entirely on local information and using a relatively simple implementation:

このアプローチの大きな利点の1つは、
その決定は、ローカルの情報に完全に基づいたクライアントタスクによって行われ、比較的単純な実装を使用していることです。

> there are no additional dependencies or latency penalties.

追加の依存性や遅延のペナルティはありません。

> One additional consideration is that client-side throttling may not work well with clients that only very sporadically send requests to their backends.

さらに考慮すべき点の1つは、クライアントサイドのスロットリングが、
非常に単発的にバックエンドにリクエストを送信するクライアントでは、うまく動作しない可能性があることです。

> In this case, the view that each client has of the state of the backend is reduced drastically, and approaches to increment this visibility tend to be expensive.

この場合、各クライアントがバックエンドの状態を持っているという見方は大幅に縮小され、
この可視性を増やすアプローチはexpensive、高コストになる傾向があります。

## Criticality
