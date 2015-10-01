define({
	id: 'cn_trad',
	name: '中文（繁體）',
	texts: {
		preferences: {
			thousandSeparator: '\u2009',
			decimalSeparator: '.'
		},
		faults: {
			101: 'The wallet file does not exist.',
			102: '錢包尚未建立。',
			103: 'Wallet file is corrupt. Please recover your wallet from a backup.',
			104: 'The provided password for the wallet is not correct.',
			105: 'No password was provided for the wallet.',
			106: '在使用一个錢包之前，它必須被打開。您必須提供正確密碼才可以打開錢包。',
			107: '錢包不含該賬戶。',
			108: '該賬戶不能被删除。因为該賬戶有大於0MEM的餘額或您要删除的賬戶是主賬戶。',
			109: '相同名稱的已經存在。請輸入其它的錢包名。',
			110: 'Wallet already contains this account.',
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
			202: '没有公鑰。',
			203: 'The account cannot be converted because not all cosignatories are known. They either need to be in the same wallet or have made at least one transaction.',
			305: 'The NEM Infrastructure Server (NIS) is not available.\n\nTry to restart the NEM software.\n\nIf you are using a remote NIS, check your configured host for typing errors or use another remote NIS.',
			306: '对不起，發生一个未知的錯誤，請重試。否則，請在NEM NIS/NCC社區內提交您的問題。',
			400: '遺失某些参數。',
			401: 'This operation cannot be completed because it might leak a private key by sending it to a remote NIS.',
			404: '啟動策略值無效。',
			500: '保存配置文件失敗。',
			600: 'NCC需要啟動NIS伺服器以執行 NEM Cloud 發送和接收交易資料。請使用NCC選單來啟動本地節點。',
			601: 'NIS節點已啟動。無法啟動第二个NIS節點。',
			602: 'Cannot perform any operations until db is fully loaded.',
			699: 'Maximum number of harvesters allowed on server has been reached.',
			700: '所提供的賬戶無法满足收割的（條件）基本標準。必須至少有10.000 vested XEM才能開始收割。',
			901: 'There was an error setting up offline mode.',
			1000: "The private key and the public key you have provided mismatch.",
			1001: 'The public key and the address you have provided mismatch.',
			1002: 'The address does not belong to the main network.',
			1203: '所提供的截止日期已过期。截止日期必須在1天之内提供。',
			1204: '所提供的截止日期超過有效期限。截止時間必須在一天之內。',
			1205: 'Your account does not have the right balance to make this transaction.',
			1206: '提供的消息文本超過容許上限，無法通过NEM發送。請减少要發送消息的內容。',
			1207: '資料庫，或未確認交易列表，已經存在此交易的散列值。',
			1208: '無法驗證本次交易的簽名。',
			1209: '此交易的ID已失效。',
			1210: '此交易的ID時間戳超過有效期限。',
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
			1321: '未知的賬戶。一个賬戶需要至少執行一个交易（作為發件人或收件人）才能在網路公開。',

		},
		common: {
			success: '成功',
			unknown: '未知狀態',
			unknownMessage: 'Ncc did not get response in a timely manner. Transaction might have been sent to the network.<br /><br />Please, check transactions before attempting to make it again.',
			appStatus: {
				nccUnknown: 'NCC status is unknown',
				nccUnavailable: 'NCC is not available',
				nccStarting: 'NCC is starting...',
				nisUnknown: 'NIS status is unknown',
				nisUnavailable: 'NIS is not available',
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
			password: '密碼',
			passwordValidation: 'Password must not be blank',
			address: 'Address',
			privateLabel: '私人標識',
			publicLabel: 'Public label',
			noCharge: 'Current account will <b>NOT</b> be charged any fees, multisig account covers them',
			fee: '費',
			multisigFee: 'Multisig fee',
			useMinimumFee: 'Use minimum fee',
			feeValidation: 'Fee must not be less than the minimum fee',
			justUse: 'Just use',
			dueBy: 'Due by',
			minutes: 'minute(s)',
			hours: 'hour(s)',
			hoursDue: 'Due by (hours)',
			hoursDueExplanation: 'If the transaction isn\'t included by the deadline, it is rejected.',
			closeButton: 'Close',
			cancelButton: 'Cancel',
			sendButton: '發送',
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
			pending: '交易待處理中',
			outgoing: '交易發送中',
			incoming: '交易接收中',
			self: '自我交易',
			importance: 'Importance transaction',
			modification: 'Aggregate Modification of Multisig',
			provision: 'Provision Namespace',
			mosaicCreation: 'Mosaic Creation',
			mosaicSupply: 'Mosaic Supply'
		},
		modals: {
			error: {
				title: '哎呀!',
				caption: '錯誤 {{1}}'
			},
			yikes: {
				title: 'Yikes!',
				caption: 'info code {{1}}'
			},
			confirmDefault: {
				yes: '是',
				no: '不'
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
				cosignatories: "Cosignatories' addresses",
				labelDesc: '此賬戶被標記為 <strong>{{1}}</strong>',
				nullLabelDesc: "此賬戶没有標記",
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
					to: '收件人',
					amount: '金額',
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
					message: '留言',
					encrypted: '消息被加密',
					noMessage: '無留言',

				}
			},
			sendNem: {
				title: '發送 XEM',
				sender: '發件人',
				thisAccount: 'This account',
				labelDesc: '此賬戶被標記為 <strong>{{1}}</strong>',
				nullLabelDesc: "此賬戶没有標記",
				amount: '金額',
				recipient: "收件人賬號",
				recipientValidation: 'Account addresses must be 40 character long excluding dashes',
				message: '信息',
				encrypt: '加密信息',
				sending: '發送...',
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
					title: '節點尚未啟動!',
					message: '請先啟動本地節點才能發送XEM!'
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
				title: '客戶信息',
				ncc: 'NEM 社區客戶端 - NCC',
				signer: '簽名者',
				remoteServer: '遠端伺服器',
				local: '本地',
				nis: 'NEM 基础設施服务器 - NIS',
				sync: '完成同步',
				notSync: '同步失敗',
				notConnected: '没有連接 NEM Cloud',
				loading: '加載中…'
			},
			transactionDetails: {
				title: '交易詳情',
				id: 'ID',
				hash: 'Hash',
				type: '交易類型',
				direction: 'Transaction Direction',
				pending: '待定',
				outgoing: '傳出',
				incoming: '接入',
				self: '本人',
				sender: '發件人',
				multisigAccount: 'Multisig Account',
				issuer: 'Issuer',
				recipient: '收件人',
				remote: 'Remote',
				multisigMessage: 'Signatures present',
				message: '信息',
				noMessage: '没信息',
				encrypted: '信息被加密',
				time: '時間戳',
				confirmations: '確認',
				confirmationsUnknown: 'Unknown',
				amount: '金額',
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
				title: "Account details",
				label: "Label",
				noLabel: "No label",
				add: "Add to address book",
				remove: "Remove from address book",
				balance: "Balance",
				vested: "vested",
				importance: "Importance",
				publicKey: "Public key",
				noPublicKey: "No public key",
				harvestedBlocks: "Harvested blocks"
			},
			bootLocalNode: {
				title: '啟動本地節點',
				account: '啟動本地節點的帳號',
				noLabel: '<span class="null">&lt;没有標識&gt;</span>',
				wallet: '錢包',
				node: '節點名稱',
				boot: '啟動',
				booting: '啟動...',
				warning: 'Boot node warning',
				warningText: 'You\'re trying to boot a node <u>{{2}}</u><br/><br/>Booting remote node is currently impossible from within NCC.',
				warningStatement: 'You have auto-boot set to true and you\'re using remote node {{3}}.<br/><br/>Booting remote node is currently impossible from within NCC',
				warningQuestion: 'Are you sure you want to boot node <u>{{3}}</u> using private key of account {{1}} ({{2}} XEM)?<br><br>This will reveal this account\'s <span class="sublabelWarning">private key</span> to node: <u>{{3}}</u>.'
			},
			closeWallet: {
				title: '關閉錢包',
				message: '您确定要關閉錢包并返回到登陸頁面?'
			},
			createAccount: {
				title: '創建新賬號',
				label: '私人標識',
				wallet: '錢包',
				successMessage: '賬號 {{1}} {{#2}}({{2}}){{/2}} 已創建!',
				create: '創建'
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
				title: '添加一个現有賬號',
				privateKey: '賬號的私鑰',
				wallet: '錢包',
				successMessage: '賬號 {{1}} {{#2}}({{2}}){{/2}} 已被添加到錢包!',
				add: '添加',
				label: '標識'
			},
			setPrimary: {
				title: '設置主賬號',
				account: '賬號被設置為主賬戶',
				noLabel: '<span class="null">&lt;物標識&gt;</span>',
				wallet: '錢包',
				successMessage: '賬戶 {{1}} {{#2}}({{2}}){{/2}} 被設置為主賬戶!',
				set: '設置為主賬戶'
			},
			changeWalletName: {
				title: '改錢包名',
				wallet: '当前錢包名',
				newName: '新錢包名',
				successMessage: '錢包名称已變更成功從<em>{{1}}</em>到<em>{{2}}</em>',
				change: '改'
			},
			changeWalletPassword: {
				title: '改錢包密碼',
				wallet: '錢包',
				password: '当前密碼',
				newPassword: '新密碼',
				confirmPassword: '確認新密碼',
				successMessage: '錢包密碼已經修改成功',
				change: '改',
				passwordNotMatchTitle: '糟!',
				passwordNotMatchMessage: '您输入的密碼和確認密碼不吻合。請正確输入您的新密碼'
			},
			changeAccountLabel: {
				title: '更改賬戶標識',
				label: '賬戶標識',
				wallet: '賬戶',
				successMessage: '賬戶{{1}}現在的標識是{{2}}',
				change: '改'
			},
			removeAccount: {
				title: '删除賬戶',
				label: '賬戶標識',
				wallet: '钱包',
				warning: '在你删除賬戶前請確認您的賬戶的MEM為零，否則会被永遠失去了',
				successMessage: '賬戶 {{1}} {{#2}}({{2}}){{/2}} 被删除了!',
				remove: '删除'
			},
			nisUnavailable: {
				title: 'NIS 不可用',
				message: '從NIS断開連，等待連接'
			},
			shutdown: {
				title: '關閉程序',
				message: '您確定要關閉NEM社區客戶端?'
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
				add: '添加'
			},
			editContact: {
				title: 'Edit contact',
				saveChanges: '保存更改'
			},
			removeContact: {
				title: 'Remove contact',
				remove: '删除'
			}
		},
		landing: {
			logo: 'images/nem_logo.png',
			importSuccess: '導入錢包成功!',
			nav: {
				start: '已经 <strong>開始</strong>',
				about: '關於 <strong>NEM</strong>',
				settings: 'Settings'
			},
			main: {
				leftTitle: '<em>NEM</em>新手?',
				leftButton: '創建新錢包',
				walletNamePlh: '錢包的名稱',
				passwordPlh: '密碼',
				confirmPasswordPlh: 'Confirm password',
				create: '建立',
				creating: 'Creating...',
				rightTitle: '已經是<em>NEM</em>玩家?',
				rightButton: '打開你的錢包',
				openButton: '打開',
				walletsFound: '發現 <strong>{{1}}</strong> <em>錢包</em>',
				copyright: '由<em>Cas Cornelissen</em>摄影'
			},
			carousel: {
				items: [
					{
						title: 'NCC 加密你的錢包',
						description: '為避免XEM被盗 <em>安全</em> 是非常重要的 &amp;。'
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
						title: 'NCC是如何工作的?',
						paragraphs: [
							'<strong>NCC</strong> 就如使用一般的錢包一樣使用XEMs。',
							'<strong>NCC</strong> 你可以使用一个<strong>NIS</strong> 伺服器。 一般是使用一个本地的伺服器 (和<strong>NCC</strong>一起安裝)',
							'您也可以連接一个遠端<strong>NIS</strong>。'
						],
						listItems: [
							'有多个錢包',
							'在一个錢包裡定里定義多个賬戶'
						]
					},
					{
						title: '什么是&#42;NIS?',
						paragraphs: [
							'負責保持<strong>NEM</strong>雲活着.',
							'The more <strong>NIS</strong> there are in the network, the better the security.,',
							'<strong>NIS</strong> 是進入 <strong>NEM</strong> 雲的入口。'
						],
						legend: '<strong>&#42;NIS</strong> 是 <strong>NEM Infrastructure Server</strong>的縮寫'
					}
				]
			},
			footer: {
				copyright: '&copy; 版權所有 2015. NEM 社區客户端。'
			}
		},
		wallet: {
			logo: 'images/nem_logo.png',
			lastAccess: '大約 {{1}} 天前',
			lastAccessJustNow: '就現在',
			lastAccessTooltip: '上次訪問是 {{1}}',
			primary: '主賬戶',
			primaryShort: 'P',
			noLabel: '<No label>',
			copiedToClipboard: '地址已複製!',
			actions: {
				refreshInfo: '信息已重新整理',
				bootLocalNode: '啟動本地節點',
				changeWalletName: '更改錢包名稱',
				changeWalletPassword: '更改錢包密碼',
				mergeWallets: '合并錢包',
				exportWallet: '匯出錢包',
				createAccount: '建立新賬號',
				createRealAccountData: 'Create real account data',
				verifyRealAccountData: 'Verify real account data',
				showPrivateKey: 'Show Account\'s PRIVATE key',
				showRemotePrivateKey: 'Show Remote Account\'s PRIVATE key',
				viewDetails: 'View Account Details',
				addAccount: '匯入一个現有賬號',
				changeAccountLabel: '更改賬戶標識',
				setPrimary: '設為主賬戶',
				removeAccount: '删除賬戶',
				clientInfo: '客戶訊息',
				closeWallet: '關閉錢包',
				closeProgram: '關閉程序',
				copyClipboard: 'Copy address to clipboard',
				copyDisabled: 'Copying an address requires flash',
				convertMultisig: 'Convert other account to multisig'
			},
			nav: [
				'儀表板',
				'消息',
				'Address Book',
				'交易',
				'收獲的礦塊',
				'資產交换',
				'新聞',
				'應用',
				'賬戶',
				'設置',
				'關閉程序'
			],
			bootNodeWarning: '一个本地節點需要啟動後才能充分利用NCC的功能。'
		},
		dashboard: {
			assets: {
				title: '你的資產'
			},
			importance: {
				title: '重要分',
				unknown: '未知狀態',
				start: '開始收割',
				harvesting: '正在收割',
				stop: '停止收割',
				description: 'NEM雲賬戶的重要',
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
				title: '最近的交易',
				sendNem: '發送XEM',
				signMultisig: 'SIGN',
				balance: '目前余額',
				loading: 'Loading balance',
				accountCosignatories: 'Multisig account',
				accountCosignatoriesView: 'view cosignatories',
				vestedBalance: 'Vested Balance',
				syncStatus: '(礦塊{{1}}{{#2}} : 估計{{3}}落後{{/2}}天)',
				notSynced: 'might be inaccurate, NIS not synchronized yet',
				unknown: 'unknown',
				columns: [
					'',
					'時間',
					'發送人/收件人',
					'留言',
					'',
					'細名',
					'確認',
					'費',
					'金額'
				],
				noMessage: '無信息',
				encrypted: '消息被加密',
				view: '查看',
				confirmationsUnknown: 'Unknown',
				pending: '待定',
				seeAll: '查看所有交易',
				noTransactions: '無交易'
			},
			nemValue: {
				title: 'XEM 值統計'
			},
			messages: {
				titleTooltip: '留言'
			},
			news: {
				titleTooltip: '新聞'
			},
			notAvailable: 'alpha版本尚未公布'
		},
		transactions: {
			title: '交易',
			sendNem: '發送 XEM',
			balance: '目前余額',
			filters: {
				confirmed: 'Confirmed',
				unconfirmed: 'Unconfirmed',
				incoming: '接受中',
				outgoing: '發送中'
			},
			table: {
				columns: [
					'',
					'時間',
					'發送人/接收人',
					'留言',
					'',
					'細節',
					'確認',
					'費',
					'金額'
				],
				noMessage: '無留言',
				encrypted: '消息被加密',
				view: '查看',
				confirmationsUnknown: 'Unknown',
				pending: '待定中',
				noTransactions: '無交易',
				loading: '載入更多的交易...'
			}
		},
		harvestedBlocks: {
			title: '收獲廣塊',
			feeEarned: '從上25塊收獲的報酬',
			unknown: 'Unknown',
			table: {
				columns: [
					'高',
					'時間',
					'礦塊列散值',
					'費'
				],
				noBlocks: '無收獲礦塊',
				loading: 'Loading more harvested blocks'
			},
			harvesting: {
				unknown: '未知狀態',
				start: '開始收割',
				harvesting: '正在收割',
				stop: '停止收割',
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
			sendNem: '發送 XEM',
			edit: 'Edit',
			remove: '删除'
		},
		settings: {
			title: '設定',
			settings: [
				{
					name: '語言'
				}
			],
			save: '保存更改',
			saveSuccess: '設置已保存成功'
		}
	}
});
