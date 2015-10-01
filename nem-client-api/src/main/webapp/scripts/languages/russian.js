define({
	id: 'ru',
	name: 'Русский',
	texts: {
		preferences: {
			thousandSeparator: '\u2009',
			decimalSeparator: '.'
		},
		faults: {
			101: 'The wallet file does not exist.',
			102: 'Кошелёк не создан.',
			103: 'Wallet file is corrupt. Please recover your wallet from a backup.',
			104: 'The provided password for the wallet is not correct.',
			105: 'No password was provided for the wallet.',
			106: 'Перед  тем, как Вы начнёте работать с кошельком, убедитесь , что он открыт. Чтобы получить доступ к кошельку, введите пароль.',
			107: 'Кошелёк не содержит этот аккаунт .',
			108: 'Аккаунт не может быть удалён .  Вероятнее всего, что аккаунт содержит на балансе более чем 0 XEM или аккаунт, который Вы пытаетесь удалить, является главным.',
			109: 'Кошелёк с таким же именем уже существует. Пожалуйста, выберете другое имя для кошелька.',
			110: 'Кошелёк уже содержит этот аккаунт .',
			111: 'The wallet name is a directory.',
			112: 'The extension of the wallet file is incorrect.',
			113: 'The wallet could not be deleted.',
			121: 'The address book file does not exist.',
			122: 'Address book has not been created.',
			123: 'Address book file is corrupt. Please recover your address book from a backup.',
			124: 'The provided password for the address book is not correct.',
			125: 'No password was provided for the address book.',
			127: 'Address book does not contain this address.',
			128: 'The address provided is not valid.',
			129: 'Another address book with the same name exists already. Please choose an other address book name.',
			130: 'Address book already contains this address.',
			131: 'The address book name is a directory.',
			132: 'The extension of the address book file is incorrect.',
			133: 'The address book could not be deleted.',
			202: 'Отсутствует public key.',
			203: 'The account cannot be converted because not all cosignatories are known. They either need to be in the same wallet or have made at least one transaction.',
			305: 'The NEM Infrastructure Server (NIS) is not available.\n\nTry to restart the NEM software.\n\nIf you are using a remote NIS, check your configured host for typing errors or use another remote NIS.',
			306: 'Произошла непредвиденная ошибка . Приносим свои извинения, повторите попытку снова. В противном случае, обратитесь за поддержкой в NEM NIS/NCC Community.',
			400: 'Некоторые параметры отсутствуют',
			401: 'Операция не может быть завершена, так как может привести к посылке Вашего private key на удалённый сервер.',
			404: 'Неправильное значение загрузки',
			500: 'Невозможно сохранить файл конфигурации',
			600: 'Для отсылки и приёма транзакций, NCC требует перезагрузку NIS сервера. Пожалуйста, используйте меню NCC для перезагрузки в локальный узел.',
			601: 'NIS узел уже запущен. Повторная попытка перезагрузки NIS невозможна.',
			602: 'Almost ready. NEM Infrastructure Server is currently loading blocks. Wallet will be functional when db is fully loaded.',
			699: 'Maximum number of harvesters allowed on server has been reached.',
			700: 'Предоставленный аккаунт не соответствует базовым требованиям для Генерации . В основном, это связанно с количеством XEM на счету.  Генерация начинается хотя бы с 10,000 vested XEM.',
			901: 'Произошла ошибка в настройках offline mode.',
			1000: 'Введённые Вами private key и  public key не соответствуют.',
			1001: 'Введённые Вами  public key и адрес  не соответствуют.',
			1002: 'Адрес не принадлежит главной сети.',
			1203: 'Предоставленный срок завершения в прошлом. Срок должен быть предоставлен в течении 1-дневного периода.',
			1204: 'Предоставленный срок завершения находится  далеко в будущем. Срок должен быть предоставлен в течении 1-дневного периода.',
			1205: 'Your account does not have the right balance to make this transaction.',
			1206: 'Предоставленный текст сообщения слишком большой для отправки через NEM. Пожалуйста, сократите текст  для его отправки.',
			1207: 'Хэш сделки уже существует или в базе данных, или в списке неподтвержденных операций.',
			1208: 'Подпись сделки не может быть проверена.',
			1209: 'Временная отметка транзакции слишком далеко в прошлом.',
			1210: 'Временная отметка транзакции слишком далеко в будущем.',
			1219: 'The transaction was rejected because the transaction cache is too full. A higher fee improves the chance that the transaction gets accepted.',
			1262: 'Delegated harvesting account has non zero balance and cannot be used.',
			1263: 'Importance transfer rejected. There is already pending importance transfer operation.',
			1264: 'Delegated harvesting is already active.',
			1265: 'Delegated harvesting is NOT active. Cannot deactivate.',
			1266: 'Importance transfer transaction (delegated harvesting) conflicts with existing transaction.',
			1271: 'Multisig signature transaction rejected. Current account is not a cosignatory of a multisig account.',
			1273: 'Multisig signature transaction rejected. Associated multisig transaction is not known to NEM network',
			1274: 'Transaction is not allowed for multisig account.',
			1275: 'Multisig account modification rejected. One of added accounts is already a cosignatory.',
			1321: 'Неизвестный аккакунт. Аккаунт должен быть частью хотя бы одной транзакции (входящей или исходящей),  чтобы быть опознанным сетью.',

		},
		common: {
			success: 'Успешно',
			unknown: 'Статус Неизвестен',
			unknownMessage: 'Ncc did not get response in a timely manner. Transaction might have been sent to the network.<br /><br />Please, check transactions before attempting to make it again.',
			appStatus: {
				nccUnknown: 'NCC status is unknown',
				nccUnavailable: 'NCC не доступен',
				nccStarting: 'NCC is starting...',
				nisUnknown: 'NIS status is unknown',
				nisUnavailable: 'NIS не доступен',
				nisStarting: 'NIS is starting...',
				notBooted: 'NIS requires to be booted. Please open your wallet and boot a local node via the popup dialog or configure the auto-boot setting.',
				loading: 'Loading blocks from db, at block: ',
				booting: 'Booting NIS...',
				nisInfoNotAvailable: 'NIS info is not avaiable yet. Trying to retrieve NIS info...',
				synchronizing: 'NIS is synchronizing. At block {{1}}, est. {{2}} behind.',
				daysBehind: {
					0: 'less than 1 day',
					1: '1 day',
					many: '{{1}} days'
				},
				synchronized: 'NIS is synchronized!',
				noRemoteNisAvailable: 'No remote NIS found in the network, disconnected from internet?'
			},
			addressBook: 'Address book',
			password: 'Пароль',
			passwordValidation: 'Password must not be blank',
			address: 'Address',
			privateLabel: 'Приватная маркировка',
			publicLabel: 'Public label',
			noCharge: 'Current account will <b>NOT</b> be charged any fees, multisig account covers them',
			fee: 'Оплата',
			multisigFee: 'Multisig fee',
			useMinimumFee: 'Use minimum fee',
			feeValidation: 'Fee must not be less than the minimum fee',
			justUse: 'Just use',
			dueBy: 'Due by',
			minutes: 'minute(s)',
			hours: 'hour(s)',
			hoursDue: 'Hours due',
			hoursDueExplanation: 'If the transaction isn\'t included by the deadline, it is rejected.',
			closeButton: 'Close',
			cancelButton: 'Cancel',
			sendButton: 'Послать',
			account: 'Account',
			thisAccount: 'This account',
			warning: 'Warning',
			newBuild: 'NEW BUILD',
			newBuildNumber: 'There is new build {{1}} available for download. Check <a class="hyperlink--default", href="http://blog.nem.io">blog.nem.io</a> for details',

		},
		transactionTypes: {
			20: 'TRANSFER TRANSACTION',
			21: 'IMPORTANCE TRANSFER',
			22: 'MODIFICATION OF MULTISIG ACCOUNT',
			23: 'PROVISION NAMESPACE',
			24: 'MOSAIC CREATION',
			25: 'MOSAIC SUPPLY',
			40: 'MULTISIG SIGNATURE',
			50: 'MULTISIG TRANSACTION',
			51: 'MULTISIG TRANSACTION',
			52: 'MULTISIG TRANSACTION',
			53: 'MULTISIG TRANSACTION',
			54: 'MULTISIG TRANSACTION',
			55: 'MULTISIG TRANSACTION',

		},
		transactionDirections: {
			pending: 'Транзакции в ожидании',
			outgoing: 'Исходящие Транзакции',
			incoming: 'Входящие Транзакции',
			self: 'Авто транзакции',
			importance: 'Importance transaction',
			modification: 'Aggregate Modification of Multisig',
			provision: 'Provision Namespace',
			mosaicCreation: 'Mosaic Creation',
			mosaicSupply: 'Mosaic Supply'
		},
		modals: {
			error: {
				title: 'Oops!',
				caption: 'ОШИБКА {{1}}'
			},
			yikes: {
				title: 'Yikes!',
				caption: 'info code {{1}}'
			},
			confirmDefault: {
				yes: 'Да',
				no: 'Нет'
			},
			initialTy: {
				title: "WELCOME to NEM",
				content: "<c>Sbhaqrq ba gur fgebat cevapvcyrf bs rtnyvgnevna naq rdhnyvgl va qvfgevohgvba, gur Arj Rpbabzl Zbirzrag, ARZ, unf abj svanyyl pbzr gb sehvgvba nsgre pybfr gb 14 zbaguf bs vagrafvir qrirybczrag. Va nqqvgvba gb 5 pber qrirybcref naq 7 pber znexrgref, jr unir n ubfg bs pbzzhavgl zrzoref jub unir urycrq hf va bar jnl be nabgure, jvgubhg jubz, guvf jbhyq arire unir pbzr gbtrgure fb jryy nf orvat bar bs gur srj pelcgb vavgvngvirf jvgu fhpu n ovt grnz. Fcrpvny zragvba vf tvira gb gur sbyybjvat:</c><ue/><c><o>Grpuavpny naq Znexrgvat vachg:</o><oe/> Nzl, naqzr, nirentrwbr, OenvaBsZnffrf, qmnezhfpu, RSSI, Rynan82, rexxv, servtrvfg, unccl4209, vafgnpnfu, wnqrqwnpx, XrivaYv, XxbgArz, xbbernz, Xelfgb, Ybv Gena, ylxn, zvkznfgre, ZeCbegZna, arzovg, akxbvy, bjba, Cnagure03, curebzbar, erabgrat.yv, evtry, FnhyTenl, funjayrnel, fbyvk, fgbar, guvyba, haibvqcy, munaxnvjra, mbngn87, 守望者, 攻陳τч酨鈊, 清风, 福泽天下</c><ue/><c><o>APP Hfre Vagresnpr genafyngvba:</o><oe/>ncrk, obrfgva, Punbf514, QVZXNMQF, svypurs, servtrvfg, Thyvire, vnzvavgabj06, Wnarn4cqn, xhccnynugv, Ypuneyrf, znegvfznegvf, zrff-yrybhpu, Cnenan, evtry, Funja, Fcvqre, 楊 輝彦</c><c><oe/>Va nqqvgvba gb gur nobir 67 grnz zrzoref, jr nyfb unir bgure zrzoref jub  pbagevohgrq, jurgure va grpuavpny, znexrgvat be fgerff grfgvat gur flfgrz qhevat gur nycun naq orgn cunfr. Jr jbhyq yvxr gb nqqvgvbanyyl gunax nyy gubfr vaqvivqhnyf abg yvfgrq urer naq gur terngre ARZ pbzzhavgl orpnhfr jvgubhg gurz, jr jbhyq unir abg rire pbzr fb sne.</c><ue/><c>Naq zbfg vzcbegnagyl<oe/><o>Gunax LBH!</o><oe/><oe/>Arj Rpbabzl fgnegf jvgu LBH!</c>"
			},
			initialBackup: {
				title: "Welcome to NEM",
				content: "You can create wallet backup from menu in upper right corner."
			},
			settings: {
				title: 'Settings',
				language: {
					label: 'Language'
				},
				remoteServer: {
					tabTitle: 'Remote Server',
					protocol: 'Protocol',
					protocolOptions: {
						http: 'HTTP'
					},
					host: 'Host',
					port: 'Port',
					defaultPort: 'Use default port.'
				},
				autoBoot: {
					tabTitle: 'Auto-boot',
					name: 'Node name',
					primaryAccount: 'Primary Account',
					auto: 'Auto boot when a wallet is opened'
				},
				save: 'Save',
				saveSuccess: 'Settings have been saved successfully'
			},
			signToken: {
				title: "Sign a token using account",
				label: "Token (url, string, anything)",
				signature: "Signed token",
				sign: "Sign"
			},
			multisig: {
				title: 'Convert account to multisig',
				multisigAccount: 'Multisig account',
				cosignatories: 'Cosignatories\' addresses',
				labelDesc: 'Этот аккаунт маркирован как {{1}}',
				nullLabelDesc: 'Этот аккаунт не имеет маркировки',
				addCosignatory: '+ Add Cosignatory',
				convert: 'Convert',
				txConfirm: {
					title: 'Confirm Conversion to Multisig Account',
					total: 'Total',

				},
				warning: 'Multisig account is on the list of cosignatories. This will result in locking down the account cutting off access to the fund. Most likely you <b>DO NOT</b> want to do that.',
				minCosignatoriesDefaultLabel: 'Use default cosignatories number',
				minCosignatoriesRelativeLabel: 'relative change',
				minCosignatoriesLabel: 'Minimum number of cosignatories',
				minCosignatoriesZero: 'Using zero would cause all cosignatories to be required',
				minCosignatoriesOverflow: 'Specified number is larger than number of cosignatories'
			},
			signMultisig: {
				title: 'Sign multisig transaction',
				original: {
					from: 'Multisig account',
					to: 'Получатель',
					amount: 'Сумма',
					fee: 'Inner Fee',
					deadline: 'Deadline'
				},
				multisigFees: 'Multisig Fees',
				multisigTotal: 'Total',
				sender: 'Cosignatory',
				passwordValidation: 'Password must not be blank',
				sending: 'Sending...',
				txConfirm: {
					title: 'Confirm Multisig Transaction',
					message: 'Сообщение',
					encrypted: 'Сообщение Зашифровано',
					noMessage: 'Нет сообщения',

				}
			},
			sendNem: {
				title: 'Послать XEM',
				sender: 'Отправитель',
				thisAccount: 'This account',
				labelDesc: 'Этот аккаунт маркирован как {{1}}',
				nullLabelDesc: 'Этот аккаунт не имеет маркировки',
				amount: 'Сумма',
				recipient: 'Аккаунт получателя',
				recipientValidation: 'Account addresses must be 40 character long excluding dashes',
				message: 'Сообщение',
				encrypt: 'Зашифрованное сообщение',
				sending: 'Посылается...',
				successMessage: 'Your transaction has been sent successfully! <br><br>Transaction hash: {{1}}',
				txConfirm: {
					title: 'Confirm Transaction',
					amount: 'Amount',
					to: 'To',
					total: 'Total',
					message: 'Message',
					encrypted: 'Message is encrypted',
					noMessage: 'No message',
					confirm: 'Confirm',
					sending: 'Sending...'
				},
				notBootedWarning: {
					title: 'Узел не был загружен!',
					message: 'Вы должны загрузить локальный узел перед тем, как Вы сможете посылать XEM!'
				},
				bootingWarning: {
					title: 'Node is being booted',
					message: 'Please wait until booting process is done to send your transaction.'
				},
				loadingWarning: {
					title: 'Loading db'
				}
			},
			clientInfo: {
				title: 'Client info',
				ncc: 'NEM Community Client - NCC',
				signer: 'Владелец подписи',
				remoteServer: 'Удалённый Сервер',
				local: 'Локальный',
				nis: 'NEM Infrastructure Server - NIS',
				sync: 'Синхронизирован',
				notSync: 'Не синхронизирован',
				notConnected: 'Не соединён с  NEM Cloud',
				loading: 'Загрузка...'
			},
			transactionDetails: {
				title: 'Детали транзакции',
				id: 'ID',
				hash: 'Хэш',
				type: 'Тип транзакции',
				direction: 'Transaction Direction',
				pending: 'В ожидании',
				outgoing: 'Исходящие',
				incoming: 'Входящие',
				self: 'Авто',
				sender: 'Отправитель',
				multisigAccount: 'Multisig Account',
				issuer: 'Issuer',
				recipient: 'Получатель',
				remote: 'Remote',
				multisigMessage: 'Signatures present',
				message: 'Сообщение',
				noMessage: 'Нет сообщений',
				encrypted: 'Сообщение зашифровано',
				time: 'Время создания',
				confirmations: 'Подтверждения',
				confirmationsUnknown: 'Unknown',
				amount: 'Сумма',
				innerFee: 'Inner Fee',
				multisigFees: 'Multisig Fees',
				cosignatory: 'Cosignatory',
				namespace: 'Namespace',
				rentalFee: 'Rental fee',
				mosaicName: 'Mosaic Name',
				description: 'Description',
				propertiesLabel: 'Properties',
				properties: {
					divisibility: 'Divisibility',
					quantity: 'Maximal quantity',
					mutablequantity: 'Is quantity mutable',
					transferable: 'Is transferable'
				},
				supplyType: 'Supply type',
				supplyAmount: 'Supply amount'
			},
			accountDetails: {
				title: 'Account details',
				label: 'Label',
				noLabel: 'No label',
				add: 'Add to address book',
				remove: 'Remove from address book',
				balance: 'Balance',
				vested: 'vested',
				importance: 'Importance',
				publicKey: 'Public key',
				noPublicKey: 'No public key',
				harvestedBlocks: 'Harvested blocks'
			},
			bootLocalNode: {
				title: 'Загрузить как локальный узел',
				account: 'Аккаунт для загрузки в локальный узел',
				noLabel: '<span class="null">&lt;Не маркированный&gt;</span>',
				wallet: 'Кошелёк',
				node: 'Имя узла',
				boot: 'Загрузить',
				booting: 'Загрузка...',
				warning: 'Boot node warning',
				warningText: 'You\'re trying to boot a node <u>{{2}}</u><br/><br/>Booting remote node is currently impossible from within NCC.',
				warningStatement: 'You have auto-boot set to true and you\'re using remote node {{3}}.<br/><br/>Booting remote node is currently impossible from within NCC',
				warningQuestion: 'Are you sure you want to boot node <u>{{3}}</u> using private key of account {{1}} ({{2}} XEM)?<br><br>This will reveal this account\'s <span class="sublabelWarning">private key</span> to node: <u>{{3}}</u>.'
			},
			closeWallet: {
				title: 'Закрыть кошелёк',
				message: 'Вы уверены, что хотите закрыть кошелёк и возвратиться на главную страницу ?'
			},
			createAccount: {
				title: 'Создать аккаунт',
				label: 'Приватная маркировка',
				wallet: 'Кошелёк',
				successMessage: 'Аккаунт {{1}} {{#2}}({{2}}){{/2}} был создан!',
				create: 'Создать'
			},
			showPrivateKey: {
				title: 'Show Account\'s PRIVATE Key',
				message: 'This will display account\'s private key on the screen, as a text. In case of any malware present in the system, this might be hazardous operation. Are you sure you want to do that?',
				publicKey: 'Public key',
				privateKey: 'Private key',
				show: 'Show the key'
			},
			showRemotePrivateKey: {
				title: 'Show Remote Account\'s PRIVATE Key',
				message: 'This will display remote account\'s private key on the screen, as a text. In case of any malware present in the system, this might be hazardous operation. Are you sure you want to do that?',

			},
			addAccount: {
				title: 'Добавить Существующий Аккаунт',
				privateKey: 'Частный Ключ Аккаунта ',
				wallet: 'Кошелёк',
				successMessage: 'Аккаунт {{1}} {{#2}}({{2}}){{/2}} был добавлен кошелёк!',
				add: 'Добавить',
				label: 'Маркировка'
			},
			setPrimary: {
				title: 'Установить как Главный Аккаунт',
				account: 'Установить аккаунт как Главный',
				noLabel: '<span class="null">&lt;нет маркировки&gt;</span>',
				wallet: 'Кошелёк',
				successMessage: 'Аккаунт {{1}} {{#2}}({{2}}){{/2}} был установлен как Главный!',
				set: 'Установлен как главный'
			},
			changeWalletName: {
				title: 'Изменить имя кошелька',
				wallet: 'Текущее имя кошелька',
				newName: 'Новое имя кошелька',
				successMessage: 'Имя кошелька было успешно изменено с <em>{{1}}</em> на <em>{{2}}</em>',
				change: 'Изменить'
			},
			changeWalletPassword: {
				title: 'Изменить пароль к  кошельку',
				wallet: 'Кошелёк',
				password: 'Текущий пароль',
				newPassword: 'Новый пароль',
				confirmPassword: 'Подтвердить новый пароль',
				successMessage: 'Пароль от кошелька был успешно изменён',
				change: 'Изменить',
				passwordNotMatchTitle: 'Oops!',
				passwordNotMatchMessage: 'Введённый Вами пароль и подтверждение пароля не совпадают. Пожалуйста убедитесь, что Вы правильно написали новый пароль.'
			},
			changeAccountLabel: {
				title: 'Изменить маркировку аккаунта',
				label: 'Маркировка аккаунта',
				wallet: 'Кошелёк',
				successMessage: 'Аккаунт {{1}} теперь маркирован как {{2}}',
				change: 'Изменить'
			},
			removeAccount: {
				title: 'Удалить аккаунт',
				label: 'Маркировка аккаунта',
				wallet: 'Кошелёк',
				warning: 'Пожалуйста  убедитесь, что Ваш аккаунт не имеет XEM монет, прежде чем Вы удалите его, или они будут потеряны навсегда.',
				successMessage: 'Аккаунт {{1}} {{#2}}({{2}}){{/2}} был удалён!',
				remove: 'Удалить'
			},
			nisUnavailable: {
				title: 'NIS недоступен',
				message: 'Отключился от NIS, жду подключения'
			},
			shutdown: {
				title: 'Закрыть программу',
				message: 'Вы уверены, что хотите закрыть NEM Community Client?'
			},
			activateDelegated: {
				title: 'Activate Delegated Harvesting',
				wallet: 'Wallet',
				activate: 'Activate',
				warningText: 'Activation will take 6 hours (360 blocks). Activation will NOT start harvesting automatically.',
				delegatedAccount: 'Delegated account public key',
				builtIn: 'built into the wallet',

			},
			deactivateDelegated: {
				title: 'Deactivate Delegated Harvesting',
				wallet: 'Wallet',
				deactivate: 'Deactivate',
				warningText: 'Deactivation will take 6 hours (360 blocks).'
			},
			startRemote: {
				title: 'Start Delegated Harvesting',
				wallet: 'Wallet',
				start: 'Start'
			},
			stopRemote: {
				title: 'Stop Delegated Harvesting',
				wallet: 'Wallet',
				stop: 'Stop'
			},
			logoutWarning: {
				leavePage: "You're leaving your wallet. Remember that if you leave your wallet this way, some others may still be able to access your wallet from this computer. To prevent that from happening, please log out using the \"Close wallet\" menu item in the top-right dropdown menu before you close the browser tab or navigate away."
			},
			addContact: {
				title: 'Add contact',
				add: 'Добавить'
			},
			editContact: {
				title: 'Edit contact',
				saveChanges: 'Сохранить настройки'
			},
			removeContact: {
				title: 'Remove contact',
				remove: 'Удалить'
			}
		},
		landing: {
			logo: 'images/nem_logo.png',
			importSuccess: 'Кошелёк успешно импортирован!',
			nav: {
				start: 'Введение',
				about: 'О NEM',
				settings: 'Settings'
			},
			main: {
				leftTitle: 'Новичок в <em>NEM</em>?',
				leftButton: 'Создать новый кошелёк',
				walletNamePlh: 'Имя Вашего кошелька',
				passwordPlh: 'Пароль',
				confirmPasswordPlh: 'Confirm password',
				create: 'Создать',
				creating: 'Creating...',
				rightTitle: 'Вы уже <em>NEM</em>ber?',
				rightButton: 'Открыть Ваш кошелёк',
				openButton: 'Открыть',
				walletsFound: 'Найдено <strong>{{1}}</strong> <em>кошелёк</em>',
				copyright: 'Фотография <em>Cas Cornelissen</em>'
			},
			carousel: {
				items: [
					{
						title: 'NCC зашифровывает Ваш кошелёк',
						description: '<em>Безопасность!</em> Важнейшая задача NEM состоит в защите Ваших XEM монет и активов.'
					},
					{
						title: 'Remote NEM Infrastructure Server',
						description: 'By using a remote NIS you don\'t have to synchronise the blockchain at start-up.',

					},
					{
						title: 'Delegated harvesting',
						description: 'With delegated harvesting you can harvest on remote NIS nodes!',

					},
					{
						title: 'Multisignature transactions',
						description: 'Secure your XEM and assets via in-blockchain multi-signature transactions.',

					},
					{
						title: 'Native language support',
						description: 'NEM user interface supports multiple languages. See "Settings".'
					},
					{
						title: 'Got any questions or feedback ?',
						description: '<a href="http://forum.ournem.com">forum.ournem.com</a> | #ournem on freenode.org | Telegram',

					}
				]
			},
			about: {
				sections: [
					{
						title: 'Как работает NCC?',
						paragraphs: [
							'<strong>NCC</strong>предоставляет доступ к Вашим XEM монетам и активам  как обычный кошелёк. Вы можете',
							'<strong>NCC</strong> требует доступ к<strong>NIS</strong>серверу для корректной работы. Локальный сервер должен быть активный ( устанавливается вместе с <strong>NCC</strong>)',
							'Вы можете сконфигурировать доступ до удалённого  <strong>NIS</strong>.'
						],
						listItems: [
							'Иметь несколько кошельков',
							'Определить несколько аккаунтов, которые будут включены в кошелёк'
						]
					},
					{
						title: 'Что такое &#42;NIS?',
						paragraphs: [
							'Этот компонент отвечает за функционирование <strong>NEM</strong>cloud.',
							'The more <strong>NIS</strong> there are in the network, the better the security.,',
							'<strong>NIS</strong>это точка доступа в<strong>NEM</strong> cloud.'
						],
						legend: '<strong>&#42;NIS</strong> расшифровывается как <strong>NEM Infrastructure Server</strong>'
					}
				]
			},
			footer: {
				copyright: '© 2015 NEM Community Client.Все права защищены.'
			}
		},
		wallet: {
			logo: 'images/nem_logo.png',
			lastAccess: 'Примерно {{1}} дней назад',
			lastAccessJustNow: 'Недавно',
			lastAccessTooltip: 'Последний доступ был {{1}}',
			primary: 'Главный',
			primaryShort: 'Г',
			noLabel: '<Без маркировки>',
			copiedToClipboard: 'Адрес был скопирован в буфер обмена!',
			actions: {
				refreshInfo: 'Обновить информацию',
				bootLocalNode: 'Загрузить Локальный Узел',
				changeWalletName: 'Изменить Имя Кошелька',
				changeWalletPassword: 'Изменить Пароль Кошелька',
				mergeWallets: 'Объединить Кошельки',
				exportWallet: 'Экспорт Кошелька',
				createAccount: 'Создать Новый Аккаунт',
				createRealAccountData: 'Create real account data',
				verifyRealAccountData: 'Verify real account data',
				showPrivateKey: 'Show Account\'s PRIVATE key',
				showRemotePrivateKey: 'Show Remote Account\'s PRIVATE key',
				viewDetails: 'View Account Details',
				addAccount: 'Добавить Существующий Аккаунт',
				changeAccountLabel: 'Изменить Маркировку Аккаунта',
				setPrimary: 'Установить как Главный Аккаунт',
				removeAccount: 'Удалить Аккаунт',
				clientInfo: 'Информация о Клиенте',
				closeWallet: 'Закрыть Кошелёк',
				closeProgram: 'Закрыть Программу',
				copyClipboard: 'Скопируйте адрес в буфер обмена',
				copyDisabled: 'Copying an address requires flash',
				convertMultisig: 'Convert other account to multisig'
			},
			nav: [
				'Панель Управления',
				'Сообщения',
				'Address Book',
				'Транзакции',
				'Собранные Блоки',
				'Обмен Активов',
				'Новости',
				'Приложения',
				'Аккаунты',
				'Настройки',
				'Закрыть Программу'
			],
			bootNodeWarning: 'Для полного функционирования NCC  нужно загрузить локальный узел.'
		},
		dashboard: {
			assets: {
				title: 'Ваши Активы'
			},
			importance: {
				title: 'Оценка Важности',
				unknown: 'Неизвестный Статус',
				start: 'Начать Сборку',
				harvesting: 'Сборка',
				stop: 'Остановить Сборку',
				description: 'Важность аккаунта для NEM cloud',
				remoteHarvest: {
					title: 'Delegated harvesting',
					activate: 'Activate delegated harvesting',
					activating: 'Activating delegated harvesting...',
					active: 'Delegated harvesting is active',
					deactivate: 'Deactivate delegated harvesting',
					deactivating: 'Deactivating delegated harvesting...',
					startRemoteHarvesting: 'Start delegated harvesting',
					remotelyHarvesting: 'Remotely harvesting',
					stopRemoteHarvesting: 'Stop delegated harvesting',
					multisigInfo: 'Activation or deactivation of a delegated harvesting for a multisig account must be done from one of cosignatory accounts',

				}
			},
			transactions: {
				title: 'Недавние Транзакции',
				sendNem: 'Послать XEM',
				signMultisig: 'SIGN',
				balance: 'Текущий Баланс',
				loading: 'Loading balance',
				accountCosignatories: 'Multisig account',
				accountCosignatoriesView: 'view cosignatories',
				vestedBalance: 'Vested Balance',
				syncStatus: '(Блок {{1}}{{#2}} : примерно {{3}} дней{{/2}})',
				notSynced: 'might be inaccurate, NIS not synchronized yet',
				unknown: 'неизвестно',
				columns: [
					'',
					'Время',
					'Отправитель/Получатель',
					'Сообщения',
					'',
					'Детали',
					'Подтверждения',
					'Оплата',
					'Сумма'
				],
				noMessage: 'Нет Сообщений',
				encrypted: 'Сообщение Зашифровано',
				view: 'Обзор',
				confirmationsUnknown: 'Unknown',
				pending: 'В ожидании',
				seeAll: 'Посмотреть все транзакции',
				noTransactions: 'Не было сделано ни одной транзакции'
			},
			nemValue: {
				title: 'Статистика XEM'
			},
			messages: {
				titleTooltip: 'Сообщения'
			},
			news: {
				titleTooltip: 'Новости'
			},
			notAvailable: 'Недоступно в Альфа версии'
		},
		transactions: {
			title: 'Транзакции',
			sendNem: 'Послать XEM',
			balance: 'Текущий Баланс',
			filters: {
				confirmed: 'Подтвержденные',
				unconfirmed: 'Неподтверждённых',
				incoming: 'Входящие',
				outgoing: 'Исходящие'
			},
			table: {
				columns: [
					'',
					'Время',
					'Отправитель/Получатель',
					'Сообщение',
					'',
					'Детали',
					'Подтверждения',
					'Оплата',
					'Сумма'
				],
				noMessage: 'Нет сообщения',
				encrypted: 'Сообщение Зашифровано',
				view: 'Обзор',
				confirmationsUnknown: 'Unknown',
				pending: 'В ожидании',
				noTransactions: 'Не было сделано ни одной транзакции',
				loading: 'Показать больше транзакций...'
			}
		},
		harvestedBlocks: {
			title: 'Сгенерированные Блоки',
			feeEarned: 'Вознаграждение за последние 25 сгенерированных блоков',
			unknown: 'Unknown',
			table: {
				columns: [
					'Блок',
					'Время',
					'Block difficulty',
					'Оплата'
				],
				noBlocks: 'Нет Сгенерированных Блоков ',
				loading: 'Посмотреть более старые сгенерированные блоки'
			},
			harvesting: {
				unknown: 'Статус Неизвестен',
				start: 'Начать Генерацию',
				harvesting: 'Генерация',
				stop: 'Остановить Генерацию',
				remoteHarvest: {
					startRemoteHarvesting: 'Start delegated harvesting',
					stopRemoteHarvesting: 'Stop delegated harvesting'
				}
			}
		},
		addressBook: {
			title: 'Address book',
			addContact: 'Add contact',
			table: {
				columns: [
					'Account address',
					'Private Label',
					'Public Label'
				],
				noContacts: 'There is no contacts in your address book'
			},
			noLabel: 'No label',
			sendNem: 'Послать XEM',
			edit: 'Edit',
			remove: 'Удалить'
		},
		settings: {
			title: 'Настройки',
			settings: [
				{
					name: 'Язык'
				}
			],
			save: 'Сохранить настройки',
			saveSuccess: 'Настройки успешно сохранены'
		}
	}
});
