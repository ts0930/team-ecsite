package jp.co.internous.rainbow.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.rainbow.model.domain.MstDestination;
import jp.co.internous.rainbow.model.mapper.MstDestinationMapper;
import jp.co.internous.rainbow.model.mapper.TblCartMapper;
import jp.co.internous.rainbow.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.rainbow.model.session.LoginSession;
	/**
	 * settlement.htmlで行われる処理を定義するクラス
	 * @author
	 */
@Controller
@RequestMapping("/rainbow/settlement")
public class SettlementController {

	@Autowired
	private TblCartMapper cartMapper;

	@Autowired
	private MstDestinationMapper destinationMapper;

	@Autowired
	private TblPurchaseHistoryMapper purchaseHistoryMapper;

	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson( );

	/**
	 * "/"を受け取った際に宛先情報を取得し
	 * settlement.htmlに表示させる関数
	 * @param .m 画面に渡すデータ
	 * @return "settlement"に遷移
	 */
	@RequestMapping("/")
	public String index(Model m) {
		int userId = loginSession.getUserId();

		List<MstDestination> destinations = destinationMapper.findByUserId(userId);
		m.addAttribute("destinations", destinations);
		//loginSession
		m.addAttribute("loginSession",loginSession);

		return "settlement";
	}

	/**
	 * settlement.htmlにて決済ボタンが押された際
	 * に機能する関数
	 * @param destinationId 宛先情報ID
	 * @return 削除数と商品ごとの決済情報が同じことを返却
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {
		Map<String,String> map = gson.fromJson(destinationId,Map.class);
		String id = map.get("destinationId");

		int userId = loginSession.getUserId();
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("destinationId", id);
		parameter.put("userId", userId);
		int insertCount = purchaseHistoryMapper.insert(parameter);

		int deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = cartMapper.deleteByUserId(userId);
		}
		return deleteCount == insertCount;
	}

}