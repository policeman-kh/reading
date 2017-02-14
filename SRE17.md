# Chapter 17. Testing for Reliability

## Types of Software Testing

> Software tests broadly fall into two categories: traditional and production.<br>

> Traditional tests are more common in software development to evaluate the correctness of software offline, during development.<br>

> Production tests are performed on a live web service to evaluate whether a deployed software system is working correctly.

ソフトウェアテストは、2つのカテゴリーに大きく分類されます。traditional and production <br>
traditional テストは、オフラインでソフトウェアの正確さを評価するソフトウェア開発でより一般的です。<br>
production テストは、オンラインのWebサービス上で実行され、デプロイされたソフトウェアシステムが正しく機能しているかを評価します。

### Traditional Tests

> As shown in Figure 17-1, traditional software testing begins with unit tests. Testing of more complex functionality is layered atop unit tests.

図17-1に示すように、traditional ソフトウェアテストは、unitテストから始まります。より複雑な機能のテストは、unitテストの上に階層化されています。

#### UNIT TESTS

> A unit test is the smallest and simplest form of software testing.<br>

> These tests are employed to assess a separable unit of software, such as a class or function, for correctness independent of the larger software system that contains the unit.<br>

> Unit tests are also employed as a form of specification to ensure that a function or module exactly performs the behavior required by the system.<br>

> Unit tests are commonly used to introduce test-driven development concepts.

ユニットテストは、ソフトウェアのテストの最小かつ最も単純な形式です。<br>
これらのテストは、ユニットを含む大規模なソフトウェアシステムの独立した正しさを評価するために、<br>
class または functionのような、ソフトウェアの分離可能なユニットを評価するために使用されます。<br>
unitテストは、functionまたはmoduleがシステムに要求される動作を正確に実行することを保証するためのform of specification、仕様の形式としても採用されています。<br>
unitテストは、test-drivenな開発の概念を導入するために一般的に使用されます。

#### INTEGRATION TESTS

> Software components that pass individual unit tests are assembled into larger components.<br>

> Engineers then run an integration test on an assembled component to verify that it functions correctly.<br>

> Dependency injection, which is performed with tools such as Dagger, is an extremely powerful technique for creating mocks of complex dependencies so that an engineer can cleanly test a component.<br>

> A common example of a dependency injection is to replace a stateful database with a lightweight mock that has precisely specified behavior.

個々のunitテストをpassするソフトウェアコンポーネントは、より大きなコンポーネントにassembled、組み立てられます。<br>
エンジニアは集約されたコンポーネントのintegrationテストを実行して、正しく機能することを確認します。<br>
Google Daggerのようなツールで実行されるDependency injectionは、エンジニアがcleanly、きれいにコンポーネントをテストすることができるように、複雑な依存関係のモックを作成するための非常に強力なテクニックです。<br>
dependency injectionの一般的な例は、ステートフルなデータベースを特定の振る舞いを正確に指定された軽量のモックで置き換えることです。

#### SYSTEM TESTS

> A system test is the largest scale test that engineers run for an undeployed system.<br>

> All modules belonging to a specific component, such as a server that passed integration tests, are assembled into the system.<br>

> Then the engineer tests the end-to-end functionality of the system. System tests come in many different flavors:

システムテストは、エンジニアがまだデプロイされていないシステムのために実行する最もスケールの大きなテストです。<br>
特定のコンポーネントに属するすべてのモジュール、例えば、integrationテストをpassしたサーバーは、システムに組み込まれます。<br>
その後、エンジニアはシステムのエンドツーエンド機能をテストします。システムテストにはたくさんの異なるflavors、趣向？種類？目的があります。

##### Smoke tests

> Smoke tests, in which engineers test very simple but critical behavior, are among the simplest type of system tests.<br>

> Smoke tests are also known as sanity testing, and serve to short-circuit additional and more expensive testing.

* Smoke test = プログラムの必須機能が正常に動作することを確認するのが目的で、コンポーネントやシステムの主要機能を網羅し、細かな点は無視するテスト。テスト部門／テスト担当者の作業効率化

Smoke testsは、エンジニアが非常にシンプルで、しかしクリティカルな振る舞いをテストする、最もシンプルなタイプのシステムテストです。<br>
Smoke testsはsanity testingとも知られ、short-circuitな追加で、よりexpensive、高価値なテストをする役割を果たします。

##### Performance tests

> Once basic correctness is established via a smoke test, a common next step is to write another variant of a system test to ensure that the performance of the system stays acceptable over the duration of its lifecycle.<br>

> Because response times for dependencies or resource requirements may change dramatically during the course of development, a system needs to be tested to make sure that it doesn’t become incrementally slower without anyone noticing (before it gets released to users).

> For example, a given program may evolve to need 32 GB of memory when it formerly only needed 8 GB, or a 10 ms response time might turn into 50 ms, and then into 100 ms.

> A performance test ensures that over time, a system doesn’t degrade or become too expensive.

スモークテストによって基本的な正しさが確立されたら、次の共通のステップは、システムのパフォーマンスが、そのライフサイクル全体にわたって許容可能な状態を保つために、システムテストのanother variant、別の変形を記述することです。<br>
dependenciesやリソース要件の応答時間は開発の過程で劇的に変化する可能性があるため、誰もは気づかないよう（ユーザーにリリースされる前に）徐々に遅くならないように、システムをテストする必要があります。<br>
たとえば、与えられたプログラムは以前は8GBしか必要なかったのみ、32GBのメモリが必要になるよう進化したり、<br>
もしくは10 msの応答時間が50msになり、さらに100 msになるかもしれません。<br>
パフォーマンステストでは、時間がたつにつれてシステムが退化しないこと、あまりにexpensive、システム処理時間が長い・リソースを多く使うようにならないことを保証します。

##### Regression tests

> Another type of system test involves preventing bugs from sneaking back into the codebase.<br>

> Regression tests can be analogized to a gallery of rogue bugs that historically caused the system to fail or produce incorrect results.<br>

> By documenting these bugs as tests at the system or integration level, engineers refactoring the codebase can be sure that they don’t accidentally introduce bugs that they’ve already invested time and effort to eliminate.<br>

もう1つのタイプのシステムテストでは、バグがコードベースに逆戻りするのを防ぎます。<br>
Regressionテストは、歴史的なシステムの失敗や不正な結果を生んだバグのギャラリーを類推することができます。<br>

これらのバグをシステムまたはintegrationレベルでのテストとして文書化することで、<br>
コードベースをリファクタリングするエンジニアは、排除するために時間と労力を投資したバグを誤って導入しないことを確信することができます。

> It’s important to note that tests have a cost, both in terms of time and computational resources.<br>

> At one extreme, unit tests are very cheap in both dimensions, as they can usually be completed in milliseconds on the resources available on a laptop.

> At the other end of the spectrum, bringing up a complete server with required dependencies (or mock equivalents) to run related tests can take significantly more time—from several minutes to multiple hours—and possibly require dedicated computing resources.

> Mindfulness of these costs is essential to developer productivity, and also encourages more efficient use of testing resources.

テストには時間と余剰リソースの両面でコストがかかることに注意することが重要です。<br>
極端なところで、unitテストは通常​​laptopの利用可能なリソースでミリ秒単位で完了できるため、両方の面で非常に安価です。<br>
一方で、関連するテストを実行するのに必要なdependencies（あるいは、モック）を備えた完全なサーバーを起動すると、<br>
数分から数時間という非常に長い時間がかかり、専用のコンピューティングリソースが必要になる可能性があります。<br>
これらのコストを心に留めておくことは、開発者の生産性にとって不可欠であり、テストリソースの効率的な使用を促進します。

#### Production Tests

> Production tests interact with a live production system, as opposed to a system in a hermetic testing environment.

> These tests are in many ways similar to black-box monitoring (see Chapter 6), and are therefore sometimes called black-box testing. Production tests are essential to running a reliable production service.

Productionテストは、hermetic、密閉されたテスト環境のシステムとは対照的に、実際に動作しているproductionシステムに影響します。<br>
これらのテストは、多くの点でブラックボックスモニタリング（第6章を参照）と似ています。したがって、ブラックボックステストと呼ばれることもあります。Productionテストは信頼できるproductionサービスを実行する上で不可欠です。

<hr>
##### ROLLOUTS ENTANGLE TESTS

ロールアウトに絡むテスト？

> It’s often said that testing is (or should be) performed in a hermetic environment [Nar12].

> This statement implies that production is not hermetic.

> Of course, production usually isn’t hermetic, because rollout cadences make live changes to the production environment in small and well-understood chunks.

密閉された環境でテストが実行されることが多いと言われています。
```
[Nar12]　Hermetic Servers：
https://testing.googleblog.com/2012/10/hermetic-servers.html

Hermetic Serversとは、ネットワーク接続のない単一のマシンで構成され、
テスト時、外部・バックエンドサーバに通信する場合はMockで構成する。
```

この声明は、productionが密閉されていないことを意味しています。<br>
もちろん、productionは密閉されていません。<br>
なぜなら、ロールアウトのリズムは、プロダクション環境を小規模でよく理解されたchunks、かたまりで、実際に変更されるからです。

> To manage uncertainty and hide risk from users, changes might not be pushed live in the same order that they were added to source control.

> Rollouts often happen in stages, using mechanisms that gradually shuffle users around, in addition to monitoring at each stage to ensure that the new environment isn’t hitting anticipated yet unexpected problems.

> As a result, the entire production environment is intentionally not representative of any given version of a binary that’s checked into source control.

uncertainty、不確定性を管理し、ユーザーへのリスクを避けるために、ソース管理に追加されたのと同じ順序・タイミングで変更を反映させることはできません。<br>

ロールアウトは、段階的に行われ、ユーザーを段階的にシャッフル？するメカニズムを使用し、<br>
加えて、新しい環境が予期しない問題を引き起こさないことを確実にするために、各段階で監視を行っています。<br>

その結果、production環境全体がソース管理にチェックインされた任意のバージョンのバイナリを故意に表していません。<br>
= chunk毎にRolloutされるため、production環境全体が特定のバージョンで構成されるわけではない？

> It’s possible for source control to have more than one version of a binary and its associated configuration file waiting to be made live.

> This scenario can cause problems when tests are conducted against the live environment.

> For example, the test might use the latest version of a configuration file located in source control along with an older version of the binary that’s live.

> Or it might test an older version of the configuration file and find a bug that’s been fixed in a newer version of the file.

ソース管理で、本番リリースされるのを待つ、1つ以上のバージョンのバイナリとそれに関連付けられたconfigurationファイルを保持することは可能です。

本番環境でテストが実行されるときに、このシナリオは問題を引き起こすことができます。

たとえば、テストでは、本番環境ののバイナリの古いバージョンと一緒に、ソースコントロールにあるconfigurationファイルの最新バージョンを使用することがあります。

あるいは、古いバージョンのconfigurationファイルをテストし、新しいバージョンのファイルで修正されたバグを見つけるかもしれません。

> Similarly, a system test can use the configuration files to assemble its modules before running the test.

> If the test passes, but its version is one in which the configuration test (discussed in the following section) fails, the result of the test is valid hermetically, but not operationally.

> Such an outcome is inconvenient.

同様に、システムテストでは、テストを実行する前にモジュールをアセンブルするためにconfigurationファイルを使用することができます。

テストはpassするが、そのバージョンがconfigurationテスト（次のセクションで説明）に失敗した場合、テストの結果は完全に正しいですが、操作的は有効ではありません。

このような結果はinconvenient、適していません。

<hr>

#### CONFIGURATION TEST

> At Google, web service configurations are described in files that are stored in our version control system.

> For each configuration file, a separate configuration test examines production to see how a particular binary is actually configured and reports discrepancies against that file.

> Such tests are inherently not hermetic, as they operate outside the test infrastructure sandbox.

Googleでは、Webサービスのconfigurationsは、バージョン管理システムに格納されているファイルに記述されています。

各configurationファイルに対して、個別のconfigurationテストでは、実際に特定のバイナリーが実際にどのように構成されているかを確認し、そのファイルに対しての矛盾を報告します。

テストインフラストラクチャのサンドボックスの外部で動作するため、このようなテストは本質的にhermetic、密閉ではありません。

> Configuration tests are built and tested for a specific version of the checked-in configuration file.

> Comparing which version of the test is passing in relation to the goal version for automation implicitly indicates how far actual production currently lags behind ongoing engineering work.

configurationテストは、チェックインされたconfigurationファイルの特定のバージョンで構築され、テストされます。

自動化のために、テストのどのバージョンがgoalとなるバージョンに関連しているか比較すると、
実際のproductionが現在進行中のエンジニアリング作業にどれだけ遅れているか、明示的に示されます。

> These nonhermetic configuration tests tend to be especially valuable as part of a distributed monitoring solution since the pattern of passes/fails across production can identify paths through the service stack that don’t have sensible combinations of the local configurations.

> The monitoring solution’s rules try to match paths of actual user requests (from the trace logs) against that set of undesirable paths.

> Any matches found by the rules become alerts that ongoing releases and/or pushes are not proceeding safely and remedial action is needed.

これらの密閉されていないconfigurationテストは、<br>
プロダクション全体でのパス/フェイルのパターンは、ローカルスタック構成の合理的な組み合わせを持たないサービススタックを通るパスを識別することができるので、<br>
distributed monitoring solution、分散監視ソリューションの一部として、特に役立ちます。

monitoringソリューションのルールは、実際のユーザーリクエストのパス（トレースログからの）を望ましくないパスのセットと照合しようとします。

ルールによって検出された一致は、進行中のリリース または/あるい プッシュが安全に進行しておらず、是正処置が必要であるというアラートになります。？

> Configuration tests can be very simple when the production deployment uses the actual file content and offers a real-time query to retrieve a copy of the content.

> In this case, the test code simply issues that query and diffs the response against the file.

> The tests become more complex when the configuration does one of the following:

production deploymentで実際のファイルコンテンツが使用され、コンテンツのコピーを取得するためのリアルタイムクエリが提供される場合、configurationテストは非常にシンプルに実施できます。

この場合、テストコードは単にそのクエリを発行し、ファイルに対する応答を比較します。

configurationが次のいずれかを実行するとき、テストはより複雑になります。

* Implicitly incorporates defaults that are built into the binary (meaning that the tests are separately versioned as a result)

バイナリに組み込まれているデフォルトを暗黙的に組み入れる（テスト結果が別々にバージョン化されていることを意味する）

* Passes through a preprocessor such as bash into command-line flags (rendering the tests subject to expansion rules)

bashなどのプリプロセッサを通じて、コマンドラインフラグに渡す（テストを拡張ルールの適用する）

* Specifies behavioral context for a shared runtime (making the tests depend on that runtime’s release schedule)

共有ランタイムの動作コンテキストを指定する（テストはそのランタイムのリリーススケジュールに依存される）

#### STRESS TEST

> In order to safely operate a system, SREs need to understand the limits of both the system and its components.

> In many cases, individual components don’t gracefully degrade beyond a certain point—instead, they catastrophically fail.

> Engineers use stress tests to find the limits on a web service.

> Stress tests answer questions such as:

安全にシステムを運用するためには、SREはシステムとそのコンポーネントの両方の限界を理解する必要があります。

多くの場合、個々のコンポーネントは特定のポイントを超えて、gracefully、優雅に退化するのではなく、壊滅的に失敗します。

エンジニアはWebサービスの制限を見つけるために、ストレステストを使用します。

ストレステストは次のような質問に答えます。

* How full can a database get before writes start to fail?

データベースはどれくらいいっぱいになると、書き込みが失敗し始めるのか？

* How many queries a second can be sent to an application server before it becomes overloaded, causing requests to fail?

1秒間に何個のクエリをアプリケーションサーバーに送信すると、アプリケーションサーバーがオーバーロードになり、リクエストに失敗するのか？

#### CANARY TEST

> The canary test is conspicuously absent from this list of production tests.

> The term canary comes from the phrase “canary in a coal mine,” and refers to the practice of using a live bird to detect toxic gases before humans were poisoned.

canary testは、production testのリストから著しく欠けています。

canaryという言葉は、「canary in a coal mine、炭鉱のカナリア」というフレーズに由来し、人間が毒される前に有毒ガスを検出するため、鳥を使用したプラクティスを参照しています。

> To conduct a canary test, a subset of servers is upgraded to a new version or configuration and then left in an incubation period.

> Should no unexpected variances occur, the release continues and the rest of the servers are upgraded in a progressive fashion.

canary testを実施するには、サーバーのサブセットを新しいバージョン、または新しいconfigurationにアップグレードし、incubation period、潜伏期間の間、放置します。

予期しない差異が発生しなければ、リリースは続行され、残りのサーバーは段階的にアップグレードされます。

> 4- A standard rule of thumb is to start by having the release impact 0.1% of user traffic, and then scaling by orders of magnitude every 24 hours while varying the geographic location of servers being upgraded (then on day 2: 1%, day 3: 10%, day 4: 100%).

4 - 標準的な経験則は、リリースがユーザートラフィックの0.1％に影響を与えることから始め、アップグレードするサーバーの地理的な位置を変えながら、24時間ごとにスケーリングします（2日目：1％、3日目： 10％、4日目：100％）。

> Should anything go awry, the single modified server can be quickly reverted to a known good state.

> We commonly refer to the incubation period for the upgraded server as “baking the binary.”

何かがうまくいかない場合は、単一の変更されたサーバーを既知の正常な状態にすばやく戻すことができます。

アップグレードされたサーバーの潜伏期間は、通常、“baking the binary” と呼ばれます。

> A canary test isn’t really a test; rather, it’s structured user acceptance.

> Whereas configuration and stress tests confirm the existence of a specific condition over deterministic software, a canary test is more ad hoc.

> It only exposes the code under test to less predictable live production traffic, and thus, it isn’t perfect and doesn’t always catch newly introduced faults.

canaryテストは実際、テストではありません。むしろ、それは構造化されたユーザーの受け入れです。

configurationテストとstressテストでは、確定的なソフトウェアに対する特定の条件の存在が確認されますが、canary testはよりad hocです。

テスト対象のコードは予測できないproductionトラフィックにしか公開されないため、パーフェクトではなく、いつも新たに導入された障害を検出するとは限りません。

> To provide a concrete example of how a canary might proceed:

> consider a given underlying fault that relatively rarely impacts user traffic and is being deployed with an upgrade rollout that is exponential.

> We expect a growing cumulative number of reported variances "CU = RK"  where  R is the rate of those reports, U is the order of the fault (defined later), and K is the period over which the traffic grows by a factor of e, or 172%. 5

canaryがどのように進んでいくかの具体的な例を提供します。

ユーザーのトラフィックに比較的ほとんど影響を与えず、急激なアップグレード、ロールアウトで展開されている根本的な障害を検討してください。

報告されたvariances、分散の累積数の増加「CU = RK」が予想されます。<br>
ここで、Rはレポートの割合、Uは障害の順序（後述）、Kはトラフィックがe倍または172% になる期間です。

> In order to avoid user impact, a rollout that triggers undesirable variances needs to be quickly rolled back to the prior configuration.

> In the short time it takes automation to observe the variances and respond, it is likely that several additional reports will be generated.

> Once the dust has settled, these reports can estimate both the cumulative number C and rate R.

ユーザーの影響を避けるために、望ましくない差異を引き起こすロールアウトは、すばやく以前の構成にロールバックする必要があります。

短時間では、variances、分散を観察して対応するために自動化が行われますが、いくつかの追加レポートが生成される可能性があります。

いったん物事が落ち着くと、これらの報告は累積数CとRの両方を推定することができます。

> Dividing and correcting for K gives an estimate of U, the order of the underlying fault. 6 Some examples:

Kを分割して訂正すると、基礎となる断層の次数であるUの推定値が得られます。

* U=1: The user’s request encountered code that is simply broken.

ユーザーのリクエストで、単純に壊れたコードが発生しました。

* U=2: This user’s request randomly damages data that a future user’s request may see.

このユーザーのリクエストは、未来のユーザーのリクエストで表示される可能性のあるデータをランダムに破損します

* U=3: The randomly damaged data is also a valid identifier to a previous request.

ランダムに損傷したデータは、以前のリクエストに対する有効な識別子でもあります

> Most bugs are of order one: they scale linearly with the amount of user traffic [Per07].

> You can generally track down these bugs by converting logs of all requests with unusual responses into new regression tests.

> This strategy doesn’t work for higher-order bugs; a request that repeatedly fails if all the preceding requests are attempted in order will suddenly pass if some requests are omitted.

> It is important to catch these higher-order bugs during release, because otherwise, operational workload can increase very quickly.

ほとんどのバグはU=1です。ユーザーのトラフィック量に比例して大きくなります。

一般的に、異常なresponseを伴うすべてのリクエストのログを新しいregressionテストに変換することで、これらのバグを追跡することができます。

この戦略はhigher-orderのバグでは機能しません。先行するすべてのリクエストが順番に試行された場合に繰り返し失敗するリクエストは、いくつかのリクエストが省略された場合に突然passするようになります。

そうしないと、operational workload、操作上の作業負荷が非常に迅速に増加する可能性があるので、リリース中にこれらのhigher-orderなバグをキャッチすることは重要です。

> Keeping the dynamics of higher- versus lower-order bugs in mind, when you are using an exponential rollout strategy, it isn’t necessary to attempt to achieve fairness among fractions of user traffic.

> As long as each method for establishing a fraction uses the same K interval, the estimate of U will be valid even though you can’t yet determine which method was instrumental in illuminating the fault.

> Using many methods sequentially while permitting some overlap keeps the value of K small.

> This strategy minimizes the total number of user-visible variances C while still allowing an early estimate of U (hoping for 1, of course).

higher-versus lower-order、上位から下位のバグの動向を念頭に置いて、指数的なロールアウト戦略を使用している場合は、ユーザートラフィックの一部に公平性を果たす必要はありません。

端数を設定する各方法が同じ K 間隔を使用する限り、どの方法が障害を照らしているのかをまだ判断できない場合でも、Uの見積もりは有効です。

いくつかのオーバーラップを許可しながら、多くのメソッドを順番に使用すると、Kの値は小さく保たれます。

この戦略は、ユーザーの視認可能な分散Cの総数を最小限に抑えながら、Uの早期推定を可能にします。（もちろん、1を希望します）。

## Creating a Test and Build Environment

> While it’s wonderful to think about these types of tests and failure scenarios on day one of a project, frequently SREs join a developer team when a project is already well underway—once the team’s project validates its research model, its library proves that the project’s underlying algorithm is scalable, or perhaps when all of the user interface mocks are finally acceptable.

> The team’s codebase is still a prototype and comprehensive testing hasn’t yet been designed or deployed.

> In such situations, where should your testing efforts begin?

> Conducting unit tests for every key function and class is a completely overwhelming prospect if the current test coverage is low or nonexistent.

> Instead, start with testing that delivers the most impact with the least effort.

プロジェクトの初日にこれらのタイプのテストと失敗シナリオを考えるのは素晴らしいことですが、プロジェクトが既に進行中の場合、頻繁にSREが開発チームに加わります。

そのチームのプロジェクトは、その研究モデルを検証し、そのライブラリはプロジェクトの基礎となるアルゴリズムがスケーラブルであり、または、おそらくすべてのユーザインターフェースモックが最終的に受け入れ可能である時点です。

チームのコードベースはまだプロトタイプであり、包括的なテストはまだ設計または展開されていません。

このような状況では、テストの作業はどこから始めるべきですか？

現在のテストカバレッジが低いか存在しない場合は、すべてのfunctionとクラスごとにunitテストを実行することはoverwhelming prospect、完全な見通し・見込みです。

代わりに、最小の労力で最も大きな影響を与えるテストから始めます。

> You can start your approach by asking the following questions:

あなたは次の質問をしてアプローチを開始できます：

* Can you prioritize the codebase in any way? To borrow a technique from feature development and project management, if every task is high priority, none of the tasks are high priority. Can you stack-rank the components of the system you’re testing by any measure of importance?

コードベースの優先順位を決めることはできますか？ <br>
featureの開発とプロジェクト管理から技術を借用するために、すべてのタスクが優先度が高い場合は優先度の高いタスクはありません。<br>
重要な尺度でテストしているシステムのコンポーネントをスタック・ランク付けできますか？

* Are there particular functions or classes that are absolutely mission-critical or business-critical? For example, code that involves billing is a commonly business-critical. Billing code is also frequently cleanly separable from other parts of the system.

ミッションクリティカルまたはビジネスクリティカルな特定のfuncionやクラスはありますか？<br>
たとえば、billingを含むコードは一般的にビジネスクリティカルです。 billingコードは、しばしばシステムの他の部分からきれいに分離可能です。

* Which APIs are other teams integrating against? Even the kind of breakage that never makes it past release testing to a user can be extremely harmful if it confuses another developer team, causing them to write wrong (or even just suboptimal) clients for your API.

他のチームと統合しているAPIはどれですか？<br>
過去のリリーステストがユーザに行わないような破壊であっても、非常に有害である可能性があり、<br>
別のデベロッパーチームを混乱させて、あなたのAPI用に間違った（あるいは最適でない）クライアントを作成することになります。

> Shipping software that is obviously broken is among the most cardinal sins of a developer.

> It takes little effort to create a series of smoke tests to run for every release.

> This type of low-effort, high-impact first step can lead to highly tested, reliable software.

明らかに壊れているソフトウェアの出荷は、開発者の最も重い罪の1つです。

すべてのリリースで実行する一連のsmokeテストを作成するのにはほとんど手間がかかりません。

このタイプのlow-effort、低労力、high-impact、インパクトの高いfirstステップは、高度にテストされた信頼性の高いソフトウェアにつながります。

> One way to establish a strong testing culture is to start documenting all reported bugs as test cases.

> If every bug is converted into a test, each test is supposed to initially fail because the bug hasn’t yet been fixed.

> As engineers fix the bugs, the software passes testing and you’re on the road to developing a comprehensive regression test suite.

強力なテスト文化を確立する1つの方法は、テストケースとして報告されたすべてのバグを文書化することです。

すべてのバグがテストに変換された場合、バグがまだ修正されていないため、最初にテストが失敗するはずです。

エンジニアがバグを修正すると、ソフトウェアはテストに合格し、包括的なregressionテストスイートを開発する道を歩んでいます。

> Another key task for creating well-tested software is to set up a testing infrastructure.

> The foundation for a strong testing infrastructure is a versioned source control system that tracks every change to the codebase.

十分にテストされたソフトウェアを作成するためのもう1つの重要なタスクは、テストインフラストラクチャをセットアップすることです。

強力なテストインフラストラクチャの基礎は、コードベースのあらゆる変更を追跡する、バージョン管理されたソース管理システムです。

> Once source control is in place, you can add a continuous build system that builds the software and runs tests every time code is submitted.

> We’ve found it optimal if the build system notifies engineers the moment a change breaks a software project.

> At the risk of sounding obvious, it’s essential that the latest version of a software project in source control is working completely.

> When the build system notifies engineers about broken code, they should drop all of their other tasks and prioritize fixing the problem.

> It is appropriate to treat defects this seriously for a few reasons:

ソース管理が完了すると、ソフトウェアをビルドし、コードがsubmitされるたびにテストを実行する継続的なビルドシステムを追加することができます。

私たちは、変更がソフトウェアプロジェクトを中断させた瞬間をエンジニアに通知するビルドシステムが最適であるとわかりました。

At the risk of sounding obvious、わかりきったことですが、<br>
ソース管理のソフトウェアプロジェクトの最新バージョンが完全に機能することが不可欠です。

ビルドシステムがエンジニアに壊れたコードを通知すると、他のすべてのタスクを中断し、問題の解決に優先順位を付けるべきです。

それはいくつかの理由でこれを深刻に扱うことが適切です：

* It’s usually harder to fix what’s broken if there are changes to the codebase after the defect is introduced.

欠陥が導入された後にコードベースが変更されると、何が壊れているのかを修正することは通常困難です。

* Broken software slows down the team because they must work around the breakage.

壊れたソフトウェアは、破損を回避する必要があるため、チームの速度を低下させます。

* Release cadences, such as nightly and weekly builds, lose their value.

夜間や週ごとのビルドのようなリリース・リズムは、その価値を失います。

* The ability of the team to respond to a request for an emergency release (for example, in response to a security vulnerability disclosure) becomes much more complex and difficult.

緊急リリースの要求（たとえば、セキュリティ脆弱性の開示に対応）に対応するチームの能力は、はるかに複雑で困難になります

> The concepts of stability and agility are traditionally in tension in the world of SRE.

> The last bullet point provides an interesting case where stability actually drives agility.

> When the build is predictably solid and reliable, developers can iterate faster!

安定性と敏捷性の概念は、伝統的にSREの世界で内包しています。

The last bullet point、最後の箇条書き？は、安定性が実際に敏捷性を促進する興味深いケースを提供します。

ビルドが予想通りに確実で信頼できるものであれば、開発者はより迅速に反復することができます。

> Some build systems like Bazel have valuable features that afford more precise control over testing.

> For example, Bazel creates dependency graphs for software projects.

> When a change is made to a file, Bazel only rebuilds the part of the software that depends on that file.

> Such systems provide reproducible builds.

> Instead of running all tests at every submit, tests only run for changed code.

> As a result, tests execute cheaper and faster.

Bazelのようなビルドシステムには、テストをより正確に制御できる貴重な機能があります。

たとえば、Bazelはソフトウェアプロジェクトのdependencyグラフを作成します。

ファイルが変更されると、Bezelはそのファイルに依存するソフトウェアの部分のみを再構築します。

このようなシステムは、再現可能なビルドを提供します。

submitごとにすべてのテストを実行するのではなく、変更されたコードに対してのみテストを実行します。

その結果、テストはより安価で高速に実行されます。

> There are a variety of tools to help you quantify and visualize the level of test coverage you need [Cra10].

> Use these tools to shape the focus of your testing:

> approach the prospect of creating highly tested code as an engineering project rather than a philosophical mental exercise.

> Instead of repeating the ambiguous refrain “We need more tests,” set explicit goals and deadlines.

必要なテストカバレッジのレベルを数値化して視覚化するのに役立つさまざまなツールがあります

これらのツールを使用して、テストのfocusを形成します。

哲学的、精神的な実行ではなく、エンジニアリングプロジェクトとして高度にテストされたコードを作成する見通しに近づきます

「もっとテストが必要です」という、あいまいなリフレインを繰り返す代わりに、明示的な目標と締め切りを設定します。

> Remember that not all software is created equal.

> Life-critical or revenue-critical systems demand substantially higher levels of test quality and coverage than a non-production script with a short shelf life.

すべてのソフトウェアが平等に作成されているわけではありません。

ライフクリティカルなシステムまたはrevenue、収益クリティカルなシステムでは、<br>
貯蔵寿命が短い非プロダクションスクリプトよりもテスト品質とカバレッジのレベルが大いに求められます
