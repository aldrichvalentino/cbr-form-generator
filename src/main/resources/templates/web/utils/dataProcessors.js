module.exports = {
  /**
   * Create data from Array input
   * @param defaultData JSON that represents default options (all false)
   * @param userData Array of user's preferred items
   *
   * @returns JSON with user's selected items
   */
  createDataFromArray: (defaultData, userData) => {
    const result = JSON.parse(JSON.stringify(defaultData));
    JSON.parse(userData).forEach(item => result[item] = true);
    return result;
  },

  /**
   * Create data from String input
   * @param defaultData JSON that represents default options (all false)
   * @param userData String, user's selected input
   *
   * @returns JSON with user's selected items
   */
  createDataFromString: (defaultData, userData) => {
    const result = JSON.parse(JSON.stringify(defaultData));
    result[userData] = true;
    return result;
  }
};
