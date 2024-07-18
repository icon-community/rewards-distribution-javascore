require("dotenv").config();
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
};

const USE_NETWORK = process.env.NETWORK;

if (USE_NETWORK === "mainnet") {
  config.default = {
    url: config.endpoint.mainnet.url,
    nid: config.endpoint.mainnet.nid,
  };
} else if (USE_NETWORK === "lisbon") {
  config.default = {
    url: config.endpoint.lisbon.url,
    nid: config.endpoint.lisbon.nid,
  };
} else {
  throw new Error("Invalid NETWORK");
}

if (config.wallet.privateKey == undefined) {
  throw new Error("PRIVATE_KEY is not set");
}
module.exports = config;
