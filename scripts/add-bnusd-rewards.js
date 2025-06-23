const fs = require("fs");
const { addBNUSDClaim, sleep, getTxResult } = require("../utils/utils");

async function main() {
  try {
    // Read the file and parse the JSON
    const users = JSON.parse(fs.readFileSync("data/users.json", "utf8"));

    for (const user of users) {
      console.log("Adding BNUSD rewards to user:", user);
      // Add the rewards to the user
      const txHash = await addBNUSDClaim(user);
      console.log("Transaction hash:", txHash);
      await sleep(4000);
      const txResult = await getTxResult(txHash);
      console.log("Transaction result:");
      console.log(txResult);
    }
  } catch (err) {
    console.log("Error running add-bnusd-rewards.js");
    console.log(err);
  }
}

main(); 