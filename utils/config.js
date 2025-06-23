require("dotenv").config();
const MAINNET = "mainnet";
const LISBON = "lisbon";
const TOKENS = {
  BALN: "BALN",
  BNUSD: "BNUSD",
};

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
    [TOKENS.BALN]: {
      [MAINNET]: "cxf61cd5a45dc9f91c15aa65831a30a90d59a09619",
      [LISBON]: "cxc3c552054ba6823107b56086134c2afc26ab1dfa",
    },
    [TOKENS.BNUSD]: {
      [MAINNET]: "cx88fd7df7ddff82f7cc735c871dc519838cb235bb",
      [LISBON]: "cx87f7f8ceaa054d46ba7343a2ecd21208e12913c6",
    },
  },
};

const USE_NETWORK = process.env.NETWORK;

if (USE_NETWORK === MAINNET) {
  config.default = {
    url: config.endpoint[MAINNET].url,
    nid: config.endpoint[MAINNET].nid,
    token: {
      BALN: config.token[TOKENS.BALN][MAINNET],
      BNUSD: config.token[TOKENS.BNUSD][MAINNET],
    },
  };
} else if (USE_NETWORK === LISBON) {
  config.default = {
    url: config.endpoint[LISBON].url,
    nid: config.endpoint[LISBON].nid,
    token: {
      BALN: config.token[TOKENS.BALN][LISBON],
      BNUSD: config.token[TOKENS.BNUSD][LISBON],
    },
  };
} else {
  throw new Error("Invalid NETWORK");
}

if (config.wallet.privateKey == undefined) {
  throw new Error("PRIVATE_KEY is not set");
}
module.exports = config;
