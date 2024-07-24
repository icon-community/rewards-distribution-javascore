require("dotenv").config();
const MAINNET = "mainnet";
const LISBON = "lisbon";
const config = {
  endpoint: {
    mainnet: {
      url: "ctz.solidwallet.io",
      nid: 1,
    },
    lisbon: {
      url: "lisbon.net.solidwallet.io",
      nid: 2,
    },
  },
  wallet: {
    privateKey: process.env.PRIVATE_KEY,
  },
  contract: {
    path: "", // path to the smart contract
    address: process.env.CONTRACT_ADDRESS,
  },
  token: {
    BALN: {
      [MAINNET]: "",
      [LISBON]: "cxc3c552054ba6823107b56086134c2afc26ab1dfa",
    },
  },
};

const USE_NETWORK = process.env.NETWORK;

if (USE_NETWORK === MAINNET) {
  config.default = {
    url: config.endpoint[MAINNET].url,
    nid: config.endpoint[MAINNET].nid,
    token: {
      BALN: config.token.BALN[MAINNET],
    },
  };
} else if (USE_NETWORK === LISBON) {
  config.default = {
    url: config.endpoint[LISBON].url,
    nid: config.endpoint[LISBON].nid,
    token: {
      BALN: config.token.BALN[LISBON],
    },
  };
} else {
  throw new Error("Invalid NETWORK");
}

if (config.wallet.privateKey == undefined) {
  throw new Error("PRIVATE_KEY is not set");
}
module.exports = config;
