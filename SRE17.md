## Types of Software Testing

>Software tests broadly fall into two categories: traditional and production.

ソフトウェアテストは、2つのカテゴリーに大きく分類されます<br>
traditional と production

>Traditional tests are more common in software development to evaluate the correctness of software offline, during development.

traditional テストは、オフラインでソフトウェアの正確さを評価するソフトウェア開発でより一般的です。

>Production tests are performed on a live web service to evaluate whether a deployed software system is working correctly.

プロダクションテストは、ライブWebサービス（オンラインのWebサービス）上で実行され、デプロイされたソフトウェアシステムが正しく機能しているかを評価します

### Traditional Tests

> As shown in Figure 17-1, traditional software testing begins with unit tests. Testing of more complex functionality is layered atop unit tests.

図17-1に示すように、traditional ソフトウェアテストはunit(単体)テストから始まります。より複雑な機能のテストは、単体テストの上に階層化されています

#### UNIT TESTS

> A unit test is the smallest and simplest form of software testing.

ユニットテストは、ソフトウェアのテストの最小かつ最も単純な形式です。

> These tests are employed to assess a separable unit of software, such as a class or function, for correctness independent of the larger software system that contains the unit.

これらのテストは、ソフトウェアの分離可能なユニットを評価するために使用されます
class または function　のような
ユニットを含む大規模なソフトウェアシステムの独立した正しさを評価するために。

> Unit tests are also employed as a form of specification to ensure that a function or module exactly performs the behavior required by the system.

ユニットテストは、functionまたはモジュールがシステムに要求される動作を正確に実行することを保証するための仕様の形式(form of specification)としても採用されている

> Unit tests are commonly used to introduce test-driven development concepts.

unitテストは、test-drivenな開発の概念を導入するために一般的に使用されます。

#### INTEGRATION TESTS

> Software components that pass individual unit tests are assembled into larger components.

個々のunitテストをpassするソフトウェアコンポーネントは、より大きなコンポーネントにアセンブル・集約されます

> Engineers then run an integration test on an assembled component to verify that it functions correctly.

エンジニアは集約されたコンポーネントのintegrationテストを実行して、正しく機能することを確認します。

> Dependency injection, which is performed with tools such as Dagger, is an extremely powerful technique for creating mocks of complex dependencies so that an engineer can cleanly test a component.

Google Daggerのようなツールで実行されるDependency injectionは、エンジニアがcleanly・きれいにコンポーネントをテストすることができるように、複雑な依存関係のモックを作成するための非常に強力なテクニックです

> A common example of a dependency injection is to replace a stateful database with a lightweight mock that has precisely specified behavior.

dependency injectionの一般的な例は、ステートフルなデータベースを、特定の振る舞いを正確に指定された軽量のモックで置き換えることです。

#### SYSTEM TESTS

> A system test is the largest scale test that engineers run for an undeployed system.

システムテストは、エンジニアがまだデプロイされていないシステムのために実行する最もスケールの大きなテストです。

> All modules belonging to a specific component, such as a server that passed integration tests, are assembled into the system.

特定のコンポーネントに属するすべてのモジュール、例えば、integrationテストをpassしたサーバーは、システムに組み込まれます。

> Then the engineer tests the end-to-end functionality of the system. System tests come in many different flavors:

その後、エンジニアはシステムのエンドツーエンド機能をテストします。システムテストにはたくさんの異なるflavors・趣向、種類・目的があります

* Smoke tests

> Smoke tests, in which engineers test very simple but critical behavior, are among the simplest type of system tests.

Smoke testsは、エンジニアが非常にシンプルで、しかしクリティカルな振る舞いをテストする、最もシンプルなタイプのシステムテストです

> Smoke tests are also known as sanity testing, and serve to short-circuit additional and more expensive testing.

Smoke testsはsanity testingとも知られ、short-circuitな追加で、より高価値なテストをする役割を果たします。

http://www.qbook.jp/qpterm/search?keyword=smoke%20test&link=1
プログラムの必須機能が正常に動作することを確認するのが目的で、コンポーネントやシステムの主要機能を網羅し、細かな点は無視するひ

* Performance tests

> Once basic correctness is established via a smoke test, a common next step is to write another variant of a system test to ensure that the performance of the system stays acceptable over the duration of its lifecycle.

スモークテストによって基本的な正しさが確立されたら、次の共通のステップは、
システムのパフォーマンスが、そのライフサイクル全体にわたって許容可能な状態を保つために
システムテストのanother variant・別の変形を記述することです

> Because response times for dependencies or resource requirements may change dramatically during the course of development, a system needs to be tested to make sure that it doesn’t become incrementally slower without anyone noticing (before it gets released to users).

dependenciesやリソース要件の応答時間は開発の過程で劇的に変化する可能性があるため、
誰もは気づかないよう（ユーザーにリリースされる前に）徐々に遅くならないようにシステムをテストする必要があります

> For example, a given program may evolve to need 32 GB of memory when it formerly only needed 8 GB, or a 10 ms response time might turn into 50 ms, and then into 100 ms.

たとえば、
与えられたプログラムは以前は8GBしか必要ない時、32GBのメモリが必要になるよう進化する、
もしくは10 msの応答時間が50msになり、さらに100 msになるかもしれない。

> A performance test ensures that over time, a system doesn’t degrade or become too expensive.

パフォーマンステストでは、時間がたつにつれてシステムが退化しないこと、あまりにも高価（システム処理時間が長い・リソースを多く使う？）にならないことを保証します

* Regression tests

> Another type of system test involves preventing bugs from sneaking back into the codebase.

もう1つのタイプのシステムテストでは、バグがコードベースに逆戻りするのを防ぎます

> Regression tests can be analogized to a gallery of rogue bugs that historically caused the system to fail or produce incorrect results.

Regressionテストは、歴史的なシステムの失敗や不正な結果を生んだ不正なバグのギャラリーを類推することができます

> By documenting these bugs as tests at the system or integration level, engineers refactoring the codebase can be sure that they don’t accidentally introduce bugs that they’ve already invested time and effort to eliminate.

これらのバグをシステムまたはintegrationレベルでのテストとして文書化することで、コードベースをリファクタリングするエンジニアは、
排除するために時間と労力を投資したバグを誤って導入しないことを確信することができます。

> It’s important to note that tests have a cost, both in terms of time and computational resources.

テストには時間と余剰リソースの両面でコストがかかることに注意することが重要です

> At one extreme, unit tests are very cheap in both dimensions, as they can usually be completed in milliseconds on the resources available on a laptop.

極端なところで、unitテストは通常​​、laptopの利用可能なリソースでミリ秒単位で完了できるため、両方の面で非常に安価です

> At the other end of the spectrum, bringing up a complete server with required dependencies (or mock equivalents) to run related tests can take significantly more time—from several minutes to multiple hours—and possibly require dedicated computing resources.

一方で、関連するテストを実行するのに必要なdependencies（あるいはモック同等物）を備えた完全なサーバーを起動すると、数分から数時間という非常に長い時間がかかり、専用のコンピューティングリソースが必要になる可能性があります

> Mindfulness of these costs is essential to developer productivity, and also encourages more efficient use of testing resources.

これらのコストを心に留めておくことは、開発者の生産性にとって不可欠であり、テストリソースの効率的な使用を促進します。

#### Production Tests

> Production tests interact with a live production system, as opposed to a system in a hermetic testing environment.

Productionテストは、密封されたテスト環境のシステムとは対照的に、実際に動作しているproductionシステムに影響します。

> These tests are in many ways similar to black-box monitoring (see Chapter 6), and are therefore sometimes called black-box testing. Production tests are essential to running a reliable production service.

これらのテストは、多くの点でブラックボックスモニタリング（第6章を参照）と似ています。したがって、ブラックボックステストと呼ばれることもあります。Productionテストは信頼できるproductionサービスを実行する上で不可欠です。

<hr>
#### ROLLOUTS ENTANGLE TESTS

ロールアウトに絡むテスト？

> It’s often said that testing is (or should be) performed in a hermetic environment [Nar12].

密封された環境でテストが実行されることが多いと言われています

https://testing.googleblog.com/2012/10/hermetic-servers.html

> This statement implies that production is not hermetic.

この声明は、productionが密封されていないことを意味しています

> Of course, production usually isn’t hermetic, because rollout cadences make live changes to the production environment in small and well-understood chunks.

もちろん、productionは密封されていません。なぜなら、ロールアウトのリズムは、プロダクション環境を小規模でよく理解されたチャンク・かたまりで、実際に変更されるからです

> To manage uncertainty and hide risk from users, changes might not be pushed live in the same order that they were added to source control.

不確定性を管理し、ユーザーへのリスクを避けるために、ソース管理に追加されたのと同じ順序・タイミングで変更を反映させることはできません。

> Rollouts often happen in stages, using mechanisms that gradually shuffle users around, in addition to monitoring at each stage to ensure that the new environment isn’t hitting anticipated yet unexpected problems.

ロールアウトは、段階的に行われ、ユーザーを段階的にシャッフルするメカニズムを使用し、
加えて新しい環境が予想外の予期しない問題を引き起こさないことを確実にするために各段階で　監視を行っている。

> As a result, the entire production environment is intentionally not representative of any given version of a binary that’s checked into source control.

その結果、production環境全体が、ソース管理にチェックインされた任意のバージョンのバイナリを故意に表していない。

> It’s possible for source control to have more than one version of a binary and its associated configuration file waiting to be made live.

ソース管理で1つ以上のバージョンのバイナリと、有効になる？のを待つ、それに関連付けられたconfigurationファイルを保持することは可能です。

> This scenario can cause problems when tests are conducted against the live environment.

このシナリオは、ライブ環境に対してテストが実行されるときに問題を引き起こすことができる

> For example, the test might use the latest version of a configuration file located in source control along with an older version of the binary that’s live.

たとえば、テストでは、ライブのバイナリの古いバージョンと一緒に、ソースコントロールにあるconfigurationファイルの最新バージョンを使用することがあります

> Or it might test an older version of the configuration file and find a bug that’s been fixed in a newer version of the file.

あるいは、古いバージョンのconfigurationファイルをテストし、新しいバージョンのファイルで修正されたバグを見つけるかもしれません

> Similarly, a system test can use the configuration files to assemble its modules before running the test.

同様に、システムテストでは、テストを実行する前に、モジュールをアセンブルするためにconfigurationファイルを使用することができる

> If the test passes, but its version is one in which the configuration test (discussed in the following section) fails, the result of the test is valid hermetically, but not operationally.

テストはpassするが、そのバージョンがconfigurationテスト（次のセクションで説明）に失敗した場合、テストの結果は完全に有効ですが、操作上は有効ではない

> Such an outcome is inconvenient.

そのような結果は不便・都合が悪い。

<hr>

#### CONFIGURATION TEST

> At Google, web service configurations are described in files that are stored in our version control system.

Googleでは、Webサービスのconfigurationsは、バージョン管理システムに格納されているファイルに記述されています

> For each configuration file, a separate configuration test examines production to see how a particular binary is actually configured and reports discrepancies against that file.

各configurationファイルに対して、個別のconfigurationテストでは、実際に特定のバイナリーが実際にどのように構成されているかを確認し、そのファイルに対しての矛盾を報告します

> Such tests are inherently not hermetic, as they operate outside the test infrastructure sandbox.

このようなテストは、テストインフラストラクチャのサンドボックスの外部で動作するため、本質的にhermetic・密閉ではありません

> Configuration tests are built and tested for a specific version of the checked-in configuration file.

configurationテストは、チェックインされたconfigurationファイルの特定のバージョンで構築され、テストされます。

> Comparing which version of the test is passing in relation to the goal version for automation implicitly indicates how far actual production currently lags behind ongoing engineering work.

自動化のために、テストのどのバージョンがgoalとなるバージョンに関連しているか比較すると、実際のproductionが進行中のエンジニアリング作業にどれだけ遅れているかが明示的に示されます？

> These nonhermetic configuration tests tend to be especially valuable as part of a distributed monitoring solution since the pattern of passes/fails across production can identify paths through the service stack that don’t have sensible combinations of the local configurations.

これらの密閉されていないconfigurationテストは、distributed monitoring solution・分散監視ソリューションの一部として特に役立ちます。
これは、production全体でのpasses/faildのパターンが、ローカルのconfigurationsの合理的な組み合わせを持たないサービススタックを通じてパスを識別できるため？

> The monitoring solution’s rules try to match paths of actual user requests (from the trace logs) against that set of undesirable paths.

monitoringソリューションのルールは、望ましくないパスのセットに反して（トレースログからの）実際のユーザーリクエストと照合しようとします。

> Any matches found by the rules become alerts that ongoing releases and/or pushes are not proceeding safely and remedial action is needed.

ルールによって検出されたこの一致は、進行中のリリース、および/または　プッシュが安全に進まず、是正措置が必要である。というアラートになる？

> Configuration tests can be very simple when the production deployment uses the actual file content and offers a real-time query to retrieve a copy of the content.

production deploymentで実際のファイルコンテンツが使用され、コンテンツのコピーを取得するためのリアルタイムクエリが提供される場合、configurationテストは非常にシンプルに実施できます

> In this case, the test code simply issues that query and diffs the response against the file.

この場合、テストコードは単にそのクエリを発行し、ファイルに対する応答を比較します。

> The tests become more complex when the configuration does one of the following:

configurationが次のいずれかを実行するとき、テストはより複雑になります。

* Implicitly incorporates defaults that are built into the binary (meaning that the tests are separately versioned as a result)

バイナリに組み込まれているデフォルトを暗黙的に組み入れる（テストが結果として、結果が別々にバージョン管理されることを意味する）

* Passes through a preprocessor such as bash into command-line flags (rendering the tests subject to expansion rules)

bashなどのプリプロセッサを通じて、コマンドラインフラグにpassする（テストを拡張ルールの対象とする）

* Specifies behavioral context for a shared runtime (making the tests depend on that runtime’s release schedule)

共有ランタイムの動作コンテキストを指定する（テストはそのランタイムのリリーススケジュールに依存される）

#### STRESS TEST

> In order to safely operate a system, SREs need to understand the limits of both the system and its components.

安全にシステムを運用するためには、SREはシステムとそのコンポーネントの両方の限界を理解する必要があります。

> In many cases, individual components don’t gracefully degrade beyond a certain point—instead, they catastrophically fail.

多くの場合、個々のコンポーネントは特定のポイントを超えて、gracefully・優雅に退化するのではなく、壊滅的に失敗します

> Engineers use stress tests to find the limits on a web service.

エンジニアはWebサービスの制限を見つけるために、ストレステストを使用します

> Stress tests answer questions such as:

ストレステストは次のような質問に答えます

* How full can a database get before writes start to fail?

データベースはどれくらいいっぱいになると、書き込みが失敗し始めるのか？

* How many queries a second can be sent to an application server before it becomes overloaded, causing requests to fail?

1秒間に何個のクエリをアプリケーションサーバーに送信すると、アプリケーションサーバーがオーバーロードになり、リクエストに失敗するのか？

#### CANARY TEST

> The canary test is conspicuously absent from this list of production tests.

canary testは、production testのリストから著しく欠けている

> The term canary comes from the phrase “canary in a coal mine,” and refers to the practice of using a live bird to detect toxic gases before humans were poisoned.

“canaryという言葉は、「canary in a coal mine、炭鉱のカナリア」というフレーズに由来し、人間が毒される前に有毒ガスを検出するため、鳥を使用したプラクティスを参照している。

> To conduct a canary test, a subset of servers is upgraded to a new version or configuration and then left in an incubation period.

canary testを実施するには、サーバーのサブセットを新しいバージョン、または新しいconfigurationにアップグレードし、incubation period、潜伏期間　放置する

> Should no unexpected variances occur, the release continues and the rest of the servers are upgraded in a progressive fashion.4

予期しない差異が発生しなければ、リリースは続行され、残りのサーバーは段階的にアップグレードされます

> 4 -  A standard rule of thumb is to start by having the release impact 0.1% of user traffic, and then scaling by orders of magnitude every 24 hours while varying the geographic location of servers being upgraded (then on day 2: 1%, day 3: 10%, day 4: 100%).

標準的な経験則では、リリースがユーザートラフィックの0.1％に影響を与えてから、アップグレードするサーバーの地理的な位置を変えながら（24時間ごとに次数をスケーリングします（2日目：1％、3日目： 10％、4日目：100％）。

> Should anything go awry, the single modified server can be quickly reverted to a known good state.

何かがうまくいかない場合は、単一の変更されたサーバーを既知の正常な状態にすばやく戻すことができます

> We commonly refer to the incubation period for the upgraded server as “baking the binary.”

アップグレードされたサーバーの潜伏期間は、通常、“baking the binary” と呼ばれます

> A canary test isn’t really a test; rather, it’s structured user acceptance.

canaryテストは実際、テストではありません。むしろ、それは構造化されたユーザーの受け入れです

> Whereas configuration and stress tests confirm the existence of a specific condition over deterministic software, a canary test is more ad hoc.

configurationテストとstressテストでは、確定的なソフトウェアに対する特定の条件の存在が確認されますが、canary testはよりad hocです。

> It only exposes the code under test to less predictable live production traffic, and thus, it isn’t perfect and doesn’t always catch newly introduced faults.

テスト対象のコードは予測できないproductionトラフィックにしか公開されないため、完全ではなく、いつも新たに導入された障害を検出するとは限りません。

> To provide a concrete example of how a canary might proceed:

canaryがどのように進んでいくかの具体的な例を提供する

> consider a given underlying fault that relatively rarely impacts user traffic and is being deployed with an upgrade rollout that is exponential.

ユーザーのトラフィックに比較的ほとんど影響を与えず、急激なアップグレード、ロールアウトで展開されている根本的な障害を検討してください

> We expect a growing cumulative number of reported variances "CU = RK"  where  R is the rate of those reports, U is the order of the fault (defined later), and K is the period over which the traffic grows by a factor of e, or 172%. 5

報告されたvariances、分散の累積数の増加「CU = RK」が予想されます。ここで、Rはレポートの割合、Uは障害の順序（後述）、Kはトラフィックがe倍または172% になる期間です

> In order to avoid user impact, a rollout that triggers undesirable variances needs to be quickly rolled back to the prior configuration.

ユーザーの影響を避けるために、望ましくない差異を引き起こすロールアウトをすばやく以前の構成にロールバックする必要があります。

> In the short time it takes automation to observe the variances and respond, it is likely that several additional reports will be generated.

短時間では、variances、分散を観察して対応するために自動化が必要ですが、いくつかの追加レポートが生成される可能性があります。

> Once the dust has settled, these reports can estimate both the cumulative number C and rate R.

いったん物事が落ち着くと、これらの報告は累積数CとRの両方を推定することができる。

> Dividing and correcting for K gives an estimate of U, the order of the underlying fault. 6 Some examples:

Kを分割して訂正すると、基礎となる断層の次数であるUの推定値が得られます。

* U=1: The user’s request encountered code that is simply broken.

ユーザーのリクエストで、単純に壊れたコードが発生しました。

* U=2: This user’s request randomly damages data that a future user’s request may see.

このユーザーのリクエストは、未来のユーザーのリクエストで表示される可能性のあるデータをランダムに破損します

* U=3: The randomly damaged data is also a valid identifier to a previous request.

ランダムに損傷したデータは、以前のリクエストに対する有効な識別子でもあります

> Most bugs are of order one: they scale linearly with the amount of user traffic [Per07].

ほとんどのバグは1 番です。ユーザーのトラフィック量に比例して大きくなります

> You can generally track down these bugs by converting logs of all requests with unusual responses into new regression tests.

一般的に、異常なresponseを伴うすべてのリクエストのログを新しいregressionテストに変換することで、これらのバグを追跡することができる

> This strategy doesn’t work for higher-order bugs; a request that repeatedly fails if all the preceding requests are attempted in order will suddenly pass if some requests are omitted.

この戦略はhigher-orderのバグでは機能しません。先行するすべてのリクエストが順番に試行された場合に繰り返し失敗するリクエストは、いくつかのリクエストが省略された場合に突然passするようになる

> It is important to catch these higher-order bugs during release, because otherwise, operational workload can increase very quickly.

そうしないと、operational workload、操作上の作業負荷が非常に迅速に増加する可能性があるので、リリース中にこれらの高次のバグをキャッチすることは重要です

> Keeping the dynamics of higher- versus lower-order bugs in mind, when you are using an exponential rollout strategy, it isn’t necessary to attempt to achieve fairness among fractions of user traffic.

higher- versus lower-order、上位から下位のバグの動向を念頭に置いて、指数的なrollout戦略を使用している場合は、ユーザートラフィックの一部の間で公平性を果たす必要はありません

> As long as each method for establishing a fraction uses the same K interval, the estimate of U will be valid even though you can’t yet determine which method was instrumental in illuminating the fault.

端数を設定する各方法が同じ K 間隔を使用する限り、どの方法が障害を照らしているのかをまだ判断できない場合でも、Uの見積もりは有効です。

> Using many methods sequentially while permitting some overlap keeps the value of K small.

いくつかのオーバーラップを許可しながら、多くのメソッドを順番に使用すると、Kの値は小さく保たれます。

> This strategy minimizes the total number of user-visible variances C while still allowing an early estimate of U (hoping for 1, of course).

この戦略は、ユーザーの視認可能な分散Cの総数を最小限に抑えながら、Uの早期推定を可能にします（もちろん、1を希望します）。

## Creating a Test and Build Environment

While it’s wonderful to think about these types of tests and failure scenarios on day one of a project, frequently SREs join a developer team when a project is already well underway—once the team’s project validates its research model, its library proves that the project’s underlying algorithm is scalable, or perhaps when all of the user interface mocks are finally acceptable.

プロジェクトの初日にこれらのタイプのテストと失敗シナリオを考えるのは素晴らしいことですが、プロジェクトがすでに進行中のときに開発チームに参加することがよくあります。

チームのプロジェクトがそのリサーチモデルを検証すると、アルゴリズムはスケーラブルであるか、またはおそらくすべてのユーザインタフェースモックが最終的に許容可能であるときである

The team’s codebase is still a prototype and comprehensive testing hasn’t yet been designed or deployed. In such situations, where should your testing efforts begin? Conducting unit tests for every key function and class is a completely overwhelming prospect if the current test coverage is low or nonexistent. Instead, start with testing that delivers the most impact with the least effort.

You can start your approach by asking the following questions:

Can you prioritize the codebase in any way? To borrow a technique from feature development and project management, if every task is high priority, none of the tasks are high priority. Can you stack-rank the components of the system you’re testing by any measure of importance?

Are there particular functions or classes that are absolutely mission-critical or business-critical? For example, code that involves billing is a commonly business-critical. Billing code is also frequently cleanly separable from other parts of the system.

Which APIs are other teams integrating against? Even the kind of breakage that never makes it past release testing to a user can be extremely harmful if it confuses another developer team, causing them to write wrong (or even just suboptimal) clients for your API.

Shipping software that is obviously broken is among the most cardinal sins of a developer. It takes little effort to create a series of smoke tests to run for every release. This type of low-effort, high-impact first step can lead to highly tested, reliable software.

One way to establish a strong testing culture7 is to start documenting all reported bugs as test cases. If every bug is converted into a test, each test is supposed to initially fail because the bug hasn’t yet been fixed. As engineers fix the bugs, the software passes testing and you’re on the road to developing a comprehensive regression test suite.

Another key task for creating well-tested software is to set up a testing infrastructure. The foundation for a strong testing infrastructure is a versioned source control system that tracks every change to the codebase.

Once source control is in place, you can add a continuous build system that builds the software and runs tests every time code is submitted. We’ve found it optimal if the build system notifies engineers the moment a change breaks a software project. At the risk of sounding obvious, it’s essential that the latest version of a software project in source control is working completely. When the build system notifies engineers about broken code, they should drop all of their other tasks and prioritize fixing the problem. It is appropriate to treat defects this seriously for a few reasons:

It’s usually harder to fix what’s broken if there are changes to the codebase after the defect is introduced.

Broken software slows down the team because they must work around the breakage.

Release cadences, such as nightly and weekly builds, lose their value.

The ability of the team to respond to a request for an emergency release (for example, in response to a security vulnerability disclosure) becomes much more complex and difficult.

The concepts of stability and agility are traditionally in tension in the world of SRE. The last bullet point provides an interesting case where stability actually drives agility. When the build is predictably solid and reliable, developers can iterate faster!

Some build systems like Bazel8 have valuable features that afford more precise control over testing. For example, Bazel creates dependency graphs for software projects. When a change is made to a file, Bazel only rebuilds the part of the software that depends on that file. Such systems provide reproducible builds. Instead of running all tests at every submit, tests only run for changed code. As a result, tests execute cheaper and faster.

There are a variety of tools to help you quantify and visualize the level of test coverage you need [Cra10]. Use these tools to shape the focus of your testing: approach the prospect of creating highly tested code as an engineering project rather than a philosophical mental exercise. Instead of repeating the ambiguous refrain “We need more tests,” set explicit goals and deadlines.

Remember that not all software is created equal. Life-critical or revenue-critical systems demand substantially higher levels of test quality and coverage than a non-production script with a short shelf life.
