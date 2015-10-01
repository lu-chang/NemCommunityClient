define({
	id: 'ro',
	name: 'Română',
	texts: {
		preferences: {
			thousandSeparator: ' ',
			decimalSeparator: '.'
		},
		faults: {
			101: 'The wallet file does not exist.',
			102: 'Portofelul nu a fost creat.',
			103: 'Wallet file is corrupt. Please recover your wallet from a backup.',
			104: 'The provided password for the wallet is not correct.',
			105: 'No password was provided for the wallet.',
			106: 'Înainte să poți lucra cu portofelul trebuie să fie deschis. Pentru a fi sigur că esti eligibil de a accesa portofelul, trebuie să introduci parola portofelului.',
			107: 'Portofelul nu conține acest cont.',
			108: 'Contul nu poate fi înlăturat. Cel mai probabil fie pentru că acest cont are un sold mai mare decât 0 XEM sau contul pe care încerci să îl înlături este contul primar.',
			109: 'Un alt portofel cu același nume există deja. Te rog alegeți un alt nume pentru portofel.',
			110: 'Portofelul conține acest cont deja.',
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
			202: 'Mesajul encriptat nu poate fi trimis pentru că destinatarul nu are nici o tranzacție efectuată.',
			203: 'The account cannot be converted because not all cosignatories are known. They either need to be in the same wallet or have made at least one transaction.',
			305: 'The NEM Infrastructure Server (NIS) is not available.\n\nTry to restart the NEM software.\n\nIf you are using a remote NIS, check your configured host for typing errors or use another remote NIS.',
			306: 'A apărut o eroare care nu a fost anticipată de echipa de dezvoltare. Ne cerem scuze, o reîncercare ar putea ajuta. În caz contrar, te rog raportează problema în comunitatea NEM NIS/NCC.',
			400: 'Un parametru lipsește sau este invalid.',
			401: 'This operation cannot be completed because it might leak a private key by sending it to a remote NIS.',
			404: 'Resursele căutate nu au fost găsite.',
			500: 'Salvarea fișerului de configurație a eșuat.',
			600: 'NCC necesită ca serverul NIS să fie pornit pentru trimiterea și primirea tranzacțiilor din NEM cloud. Te rog folosește opțiunea din meniul NCC pentru pornirea nodului local.',
			601: 'Nodul NIS este deja pornit. O a doua încercare de a porni nodul NIS nu este posibilă.',
			602: 'Almost ready. NEM Infrastructure Server is currently loading blocks. Wallet will be functional when db is fully loaded.',
			699: 'Maximum number of harvesters allowed on server has been reached.',
			700: 'Contul furnizat nu satisface criteriul de bază pentru recoltare. În mod special, problema are legatură cu suma de XEM disponibilă în cont. Recoltarea poate începe cu minim 10000 vested XEM.',
			901: 'O eroare a apărut la setarea nodului offline.',
			1000: 'Cheia privată și cheia publică care au fost introduse nu se potrivesc.',
			1001: 'Cheia publică și adresa care au fost introduse nu se potrivesc.',
			1002: 'The address does not belong to the main network.',
			1203: 'Data scadentă furnizată este din trecut. Scadența trebuie introdusă cu o perioadă de grație de o zi.',
			1204: 'Data scadentă introdusă este prea îndepărtată în viitor. Scadența trebuie introdusă cu o perioadă de grație de o zi.',
			1205: 'Your account does not have the right balance to make this transaction.',
			1206: 'Textul din mesajul introdus este prea mare pentru a putea fi trimis prin NEM. Te rog încercă să reduci lungimea mesajului pe care dorești să îl trimiți.',
			1207: 'Hashul tranzacției există deja în baza de date sau în lista de tranzacții neconfirmate.',
			1208: 'Semnătura tranzacției nu a putut fi verificată.',
			1209: 'Data si ora tranzacției sunt prea îndepărtate în trecut.',
			1210: 'Data si ora tranzacției sunt prea îndepărtate în viitor.',
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
			1321: 'Contul este necunoscut. Un cont trebuie să facă parte din măcar o tranzacție (expeditor sau destinatar) pentru a fi cunoscut în rețea.',

		},
		common: {
			success: 'Succes',
			unknown: 'Status necunoscut',
			unknownMessage: 'Ncc did not get response in a timely manner. Transaction might have been sent to the network.<br /><br />Please, check transactions before attempting to make it again.',
			appStatus: {
				nccUnknown: 'Statusul NCC este necunoscut',
				nccUnavailable: 'NCC nu este disponibil',
				nccStarting: 'NCC pornește...',
				nisUnknown: 'Statusul NIS este necunoscut',
				nisUnavailable: 'NIS nu este disponibil',
				nisStarting: 'NIS pornește...',
				notBooted: 'NIS are necesită să fie pornit. Te rog deschide portofelul și pornește un nod local prin fereastra care apare sau configurează setarea de auto-pornire.',
				loading: 'Loading blocks from db, at block: ',
				booting: 'NIS se pornește...',
				nisInfoNotAvailable: 'Informația NIS nu este disponibilă încă. Se încearcă a prelua informația NIS...',
				synchronizing: 'NIS se sincronizează. La block-ul {{1}}, est. {{2}} zile în urmă.',
				daysBehind: {
					0: 'Mai puțin de 1 zi în urmă',
					1: '1 zi',
					many: '{{1}} zile'
				},
				synchronized: 'NIS este sincronizat!',
				noRemoteNisAvailable: 'No remote NIS found in the network, disconnected from internet?'
			},
			addressBook: 'Address book',
			password: 'Parola',
			passwordValidation: 'Password must not be blank',
			address: 'Adresa',
			privateLabel: 'Etichetă privată',
			publicLabel: 'Public label',
			noCharge: 'Current account will <b>NOT</b> be charged any fees, multisig account covers them',
			fee: 'Taxă',
			multisigFee: 'Multisig fee',
			useMinimumFee: 'Use minimum fee',
			feeValidation: 'Fee must not be less than the minimum fee',
			justUse: 'Just use',
			dueBy: 'Due by',
			minutes: 'minute(s)',
			hours: 'hour(s)',
			hoursDue: 'Scadență',
			hoursDueExplanation: 'If the transaction isn\'t included by the deadline, it is rejected.',
			closeButton: 'Close',
			cancelButton: 'Cancel',
			sendButton: 'Trimite',
			account: 'Cont',
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
			pending: 'Tranzacție în așteptare',
			outgoing: 'Tranzacție de trimis',
			incoming: 'Tranzacție de primit',
			self: 'Tranzacție proprie',
			importance: 'Importance transaction',
			modification: 'Aggregate Modification of Multisig',
			provision: 'Provision Namespace',
			mosaicCreation: 'Mosaic Creation',
			mosaicSupply: 'Mosaic Supply'
		},
		modals: {
			error: {
				title: 'Oops!',
				caption: 'ERROARE {{1}}'
			},
			yikes: {
				title: 'Yikes!',
				caption: 'info code {{1}}'
			},
			confirmDefault: {
				yes: 'Da',
				no: 'Nu'
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
				title: 'Setări',
				language: {
					label: 'Limbă'
				},
				remoteServer: {
					tabTitle: 'Remote Server',
					protocol: 'Protocol',
					protocolOptions: {
						http: 'HTTP'
					},
					host: 'Gazdă',
					port: 'Port',
					defaultPort: 'Use default port.'
				},
				autoBoot: {
					tabTitle: 'Auto-pornire',
					name: 'Numele nodului',
					primaryAccount: 'Primar Cont',
					auto: 'Pornește automat când un portofel a fost deschis'
				},
				save: 'Salvează',
				saveSuccess: 'Setările au fost salvate cu succes'
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
				labelDesc: 'Eticheta contului este <strong>{{1}}</strong>',
				nullLabelDesc: 'Acest cont nu are o etichetă',
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
					to: 'Destinatar',
					amount: 'Sumă',
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
					message: 'Mesaj',
					encrypted: 'Mesajul este encriptat',
					noMessage: 'Fără mesaj',

				}
			},
			sendNem: {
				title: 'Trimite XEM',
				sender: 'Expeditor',
				thisAccount: 'This account',
				labelDesc: 'Eticheta contului este <strong>{{1}}</strong>',
				nullLabelDesc: 'Acest cont nu are o etichetă',
				amount: 'Sumă',
				recipient: 'Contul destinatarului',
				recipientValidation: 'Account addresses must be 40 character long excluding dashes',
				message: 'Mesaj',
				encrypt: 'Mesaj encriptat',
				sending: 'Se trimite...',
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
					title: 'Nodul nu a fost pornit!',
					message: 'Un nod local trebuie să fie pornit pentru a putea trimite XEM!'
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
				signer: 'Semnat',
				remoteServer: 'Remote Server',
				local: 'Local',
				nis: 'NEM Infrastructure Server - NIS',
				sync: 'Sincronizat',
				notSync: 'Nesincronizat',
				notConnected: 'Neconectat la NEM Cloud',
				loading: 'Se încarcă...'
			},
			transactionDetails: {
				title: 'Detalii tranzacție',
				id: 'ID',
				hash: 'Hash',
				type: 'Tipul de tranzacție',
				direction: 'Transaction Direction',
				pending: 'În așteptare',
				outgoing: 'De trimis',
				incoming: 'De primit',
				self: 'Sine',
				sender: 'Expeditor',
				multisigAccount: 'Multisig Account',
				issuer: 'Issuer',
				recipient: 'Destinatar',
				remote: 'Remote',
				multisigMessage: 'Signatures present',
				message: 'Mesaj',
				noMessage: 'Nici un mesaj',
				encrypted: 'Mesajul este encriptat',
				time: 'Data și ora',
				confirmations: 'Confirmări',
				confirmationsUnknown: 'Unknown',
				amount: 'Sumă',
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
				title: 'Pornește nodul local',
				account: 'Cont pentru a porni nodul local',
				noLabel: '<span class=\'null\'><Fără etichetă></span>',
				wallet: 'Portofel',
				node: 'Numele nodului',
				boot: 'Pornește',
				booting: 'Se pornește...',
				warning: 'Boot node warning',
				warningText: 'You\'re trying to boot a node <u>{{2}}</u><br/><br/>Booting remote node is currently impossible from within NCC.',
				warningStatement: 'You have auto-boot set to true and you\'re using remote node {{3}}.<br/><br/>Booting remote node is currently impossible from within NCC',
				warningQuestion: 'Are you sure you want to boot node <u>{{3}}</u> using private key of account {{1}} ({{2}} XEM)?<br><br>This will reveal this account\'s <span class="sublabelWarning">private key</span> to node: <u>{{3}}</u>.'
			},
			closeWallet: {
				title: 'Închide portofelul',
				message: 'Ești sigur că dorești să închizi portofelul și să te intorci la pagina de început?'
			},
			createAccount: {
				title: 'Crează portofel nou',
				label: 'Etichetă privată',
				wallet: 'Portofel',
				successMessage: 'Contul {{1}} {{#2}}({{2}}){{/2}} a fost creat!',
				create: 'Crează'
			},
			showPrivateKey: {
				title: 'Show Account\'s PRIVATE Key',
				message: 'This will display account\'s private key on the screen, as a text. In case of any malware present in the system, this might be hazardous operation. Are you sure you want to do that?',
				publicKey: 'Cheia publică',
				privateKey: 'Cheia privată',
				show: 'Show the key'
			},
			showRemotePrivateKey: {
				title: 'Show Remote Account\'s PRIVATE Key',
				message: 'This will display remote account\'s private key on the screen, as a text. In case of any malware present in the system, this might be hazardous operation. Are you sure you want to do that?',

			},
			addAccount: {
				title: 'Adaugă un cont existent',
				privateKey: 'Cheia privată a contului',
				wallet: 'Portofel',
				successMessage: 'Contul {{1}} {{#2}}({{2}}){{/2}} a fost adăugat în portofel!',
				add: 'Adaugă',
				label: 'Etichetă'
			},
			setPrimary: {
				title: 'Stabilește cont primar',
				account: 'Cont pentru a fi stabilit ca primar',
				noLabel: '<span class=\'null\'><Fără etichetă></span>',
				wallet: 'Portofel',
				successMessage: 'Contul {{1}} {{#2}}({{2}}){{/2}} a fost stabilit ca primar!',
				set: 'Stabilește ca primar'
			},
			changeWalletName: {
				title: 'Schimbă numele portofelului',
				wallet: 'Numele actual al portofelului',
				newName: 'Numele nou al portofelului',
				successMessage: 'Numele portofelului a fost schimbat cu succes de la <em>{{1}}</em> la <em>{{2}}</em>',
				change: 'Schimbă'
			},
			changeWalletPassword: {
				title: 'Schimbă parola portofelului',
				wallet: 'Portofel',
				password: 'Parola actuală',
				newPassword: 'Parola nouă',
				confirmPassword: 'Confirmă parola nouă',
				successMessage: 'Parola portofelului a fost schimbată cu succes',
				change: 'Schimbă',
				passwordNotMatchTitle: 'Oops!',
				passwordNotMatchMessage: 'Parola introdusă si confirmarea parolei nu se potrivesc. Te rog asigură-te că introduci noua parolă corect.'
			},
			changeAccountLabel: {
				title: 'Schimbă eticheta contului',
				label: 'Eticheta contului',
				wallet: 'Portofel',
				successMessage: 'Contul {{1}} este etichetat {{2}}',
				change: 'Schimbă'
			},
			removeAccount: {
				title: 'Înlătură contul',
				label: 'Eticheta contului',
				wallet: 'Portofel',
				warning: 'Te rog să asigură-te că contul tău nu mai are nici un XEM în el înainte să îl înlături, în caz contrar aceștia vor fi pierduți pentru totdeauna.',
				successMessage: 'Contul {{1}} {{#2}}({{2}}){{/2}} a fost înlăturat!',
				remove: 'Înlătură'
			},
			nisUnavailable: {
				title: 'NIS indisponibil',
				message: 'Deconectat de la NIS, se așteaptă conexiunea'
			},
			shutdown: {
				title: 'Închide programul',
				message: 'Ești sigur că dorești să închizi NEM Community Client?'
			},
			activateDelegated: {
				title: 'Activate Delegated Harvesting',
				wallet: 'Portofel',
				activate: 'Activează',
				warningText: 'Activation will take 6 hours (360 blocks). Activation will NOT start harvesting automatically.',
				delegatedAccount: 'Delegated account public key',
				builtIn: 'built into the wallet',

			},
			deactivateDelegated: {
				title: 'Deactivate Delegated Harvesting',
				wallet: 'Portofel',
				deactivate: 'Dezactivează',
				warningText: 'Deactivation will take 6 hours (360 blocks).'
			},
			startRemote: {
				title: 'Start Delegated Harvesting',
				wallet: 'Portofel',
				start: 'Pornește'
			},
			stopRemote: {
				title: 'Stop Delegated Harvesting',
				wallet: 'Portofel',
				stop: 'Oprește'
			},
			logoutWarning: {
				leavePage: "You're leaving your wallet. Remember that if you leave your wallet this way, some others may still be able to access your wallet from this computer. To prevent that from happening, please log out using the \"Close wallet\" menu item in the top-right dropdown menu before you close the browser tab or navigate away."
			},
			addContact: {
				title: 'Add contact',
				add: 'Adaugă'
			},
			editContact: {
				title: 'Edit contact',
				saveChanges: 'Salvează schimbările'
			},
			removeContact: {
				title: 'Remove contact',
				remove: 'Înlătură'
			}
		},
		landing: {
			logo: 'images/nem_logo.png',
			importSuccess: 'Portofelul a fost importat cu succes!',
			nav: {
				start: 'Primii<strong>Pași</strong>',
				about: 'Despre <strong>NEM</strong>',
				settings: '<strong>Setări</strong>'
			},
			main: {
				leftTitle: 'Nou în <em>NEM</em>?',
				leftButton: 'Crează portofel nou',
				walletNamePlh: 'Numele portofelului tău',
				passwordPlh: 'Parola',
				confirmPasswordPlh: 'Confirm password',
				create: 'Crează',
				creating: 'Creating...',
				rightTitle: 'Ești deja un <em>NEM</em>bru?',
				rightButton: 'Deschide portofelul',
				openButton: 'Deschide',
				walletsFound: '<strong>{{1}}</strong> <em>portofelul</em> a fost găsit',
				copyright: 'Fotografie de <em>Cas Cornelissen</em>'
			},
			carousel: {
				items: [
					{
						title: 'NCC îți encripteaza portofelul',
						description: '<em>Securitatea</em> este foarte importantă în NEM pentru a evita furtul de monede XEM &amp; active.'
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
						title: 'Cum funcționează NCC?',
						paragraphs: [
							'<strong>NCC</strong> facilitează accesul către activele și monedele tale la fel ca portofelele tradiționale. Ai voie',
							'<strong>NCC</strong> necesită acces către <strong>NIS</strong> server pentru a putea funcționa. Standardul este să ai un nod local activ (este instalat împreună cu <strong>NCC</strong>)',
							'Poți deasemeni să configurezi accesul către un remote <strong>NIS</strong>.'
						],
						listItems: [
							'Ai multiple portofele',
							'Definește multiple conturi pentru a fi introduse într-un portofel'
						]
					},
					{
						title: 'Ce este &#42;NIS?',
						paragraphs: [
							'Acest component este responsabil de a ține <strong>NEM</strong> cloud-ul activ.',
							'The more <strong>NIS</strong> there are in the network, the better the security.,',
							'<strong>NIS</strong> este punctul de acces către <strong>NEM</strong> cloud.'
						],
						legend: '<strong>&#42;NIS</strong> înseamnă <strong>NEM Infrastructure Server</strong>'
					}
				]
			},
			footer: {
				copyright: '&copy; Copyright 2015. NEM Community Client.'
			}
		},
		wallet: {
			logo: 'images/nem_logo.png',
			lastAccess: 'Aproximativ cu {{1}} zile în urmă',
			lastAccessJustNow: 'Chiar acum',
			lastAccessTooltip: 'Ultima accesare a fost {{1}}',
			primary: 'primar',
			primaryShort: 'P',
			noLabel: '<Fără etichetă>',
			copiedToClipboard: 'Adresa a fost copiată în clipboard!',
			actions: {
				refreshInfo: 'Actualizează informația',
				bootLocalNode: 'Pornește nodul local',
				changeWalletName: 'Schimbă numele portofelului',
				changeWalletPassword: 'Schimbă parola portofelului',
				mergeWallets: 'Unește portofele',
				exportWallet: 'Exportă portofelul',
				createAccount: 'Crează cont nou',
				createRealAccountData: 'Crează date reale de cont',
				verifyRealAccountData: 'Verifică datele contului real',
				showPrivateKey: 'Show Account\'s PRIVATE key',
				showRemotePrivateKey: 'Show Remote Account\'s PRIVATE key',
				viewDetails: 'View Account Details',
				addAccount: 'Adaugă un nou cont existent',
				changeAccountLabel: 'Schimbă eticheta contului',
				setPrimary: 'Stabilște ca cont primar',
				removeAccount: 'Înlătură cont',
				clientInfo: 'Client Info',
				closeWallet: 'Închide portofelul',
				closeProgram: 'Închide programul',
				copyClipboard: 'Copiază adresa în clipboard',
				copyDisabled: 'Copying an address requires flash',
				convertMultisig: 'Convert other account to multisig'
			},
			nav: [
				'Panou principal',
				'Mesaje',
				'Address Book',
				'Tranzacții',
				'Blocuri recoltate',
				'Bursă',
				'Știri',
				'Aplicații',
				'Conturi',
				'Setări',
				'Închide program'
			],
			bootNodeWarning: 'Un nod local trebuie să fie pornit pentru a putea folosi proprietățile NCC.'
		},
		dashboard: {
			assets: {
				title: 'Activele tale'
			},
			importance: {
				title: 'Scorul importanței',
				unknown: 'Status necunoscut',
				start: 'Începe recoltarea',
				harvesting: 'Se recoltează',
				stop: 'Oprește recoltarea',
				description: 'Importanța contului pentru NEM cloud',
				remoteHarvest: {
					title: 'Delegated harvesting',
					activate: 'Activate delegated harvesting',
					activating: 'Activating delegated harvesting...',
					active: 'Delegated harvesting is active',
					deactivate: 'Deactivate delegated harvesting',
					deactivating: 'Deactivating delegated harvesting...',
					startRemoteHarvesting: 'Start delegated harvesting',
					remotelyHarvesting: 'Se recoltează de la distanță',
					stopRemoteHarvesting: 'Stop delegated harvesting',
					multisigInfo: 'Activation or deactivation of a delegated harvesting for a multisig account must be done from one of cosignatory accounts',

				}
			},
			transactions: {
				title: 'Tranzacții recente',
				sendNem: 'Trimite XEM',
				signMultisig: 'SIGN',
				balance: 'Sold actual',
				loading: 'Loading balance',
				accountCosignatories: 'Multisig account',
				accountCosignatoriesView: 'view cosignatories',
				vestedBalance: 'Vested Balance',
				syncStatus: '(la block-ul {{1}}{{#2}} : est. {{3}} zile în urmă{{/2}})',
				notSynced: 'might be inaccurate, NIS not synchronized yet',
				unknown: 'necunoscut',
				columns: [
					'',
					'Timp',
					'Expeditor/Destinatar',
					'Mesaj',
					'',
					'Detalii',
					'Confirmări',
					'Taxă',
					'Sumă'
				],
				noMessage: 'Fără mesaj',
				encrypted: 'Mesajul este encriptat',
				view: 'Vizualizează',
				confirmationsUnknown: 'Unknown',
				pending: 'În așteptare',
				seeAll: 'Vizualizează toate tranzacțiile',
				noTransactions: 'Nu a fost efectuată nici o tranzacție până acum'
			},
			nemValue: {
				title: 'Statistici despre valoarea XEM'
			},
			messages: {
				titleTooltip: 'Mesaje'
			},
			news: {
				titleTooltip: 'Știri'
			},
			notAvailable: 'Nu este încă disponibil în versiunea beta'
		},
		transactions: {
			title: 'Tranzacții',
			sendNem: 'Trimite XEM',
			balance: 'Sold actual',
			filters: {
				confirmed: 'Confirmat',
				unconfirmed: 'Neconfirmat',
				incoming: 'De primit',
				outgoing: 'De trimis'
			},
			table: {
				columns: [
					'',
					'Timp',
					'Expeditor/Destinatar',
					'Mesaj',
					'',
					'Detalii',
					'Confirmări',
					'Taxă',
					'Sumă'
				],
				noMessage: 'Fără mesaj',
				encrypted: 'Mesajul este encriptat',
				view: 'Vizualizează',
				confirmationsUnknown: 'Unknown',
				pending: 'În așteptare',
				noTransactions: 'Nu a fost efectuată nici o tranzacție până acum',
				loading: 'Se încarcă mai multe tranzacții...'
			}
		},
		harvestedBlocks: {
			title: 'Block-uri recoltate',
			feeEarned: 'Taxe câștigate din ultimele 25 de blockuri recoltate',
			unknown: 'Unknown',
			table: {
				columns: [
					'Block',
					'Timp',
					'Block difficulty',
					'Taxă'
				],
				noBlocks: 'Niciun block nu a fost recoltat încă',
				loading: 'Se încarcă mai multe blocuri recoltate'
			},
			harvesting: {
				unknown: 'Status necunoscut',
				start: 'Începe recoltarea',
				harvesting: 'Se recoltează',
				stop: 'Oprește recoltarea',
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
			sendNem: 'Trimite XEM',
			edit: 'Edit',
			remove: 'Înlătură'
		},
		settings: {
			title: 'Setări',
			settings: [
				{
					name: 'Limbă'
				}
			],
			save: 'Salvează schimbările',
			saveSuccess: 'Setările au fost salvate cu succes'
		}
	}
});
