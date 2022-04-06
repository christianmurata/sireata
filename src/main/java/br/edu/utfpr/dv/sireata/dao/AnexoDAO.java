package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Anexo;

public class AnexoDAO {
	
	public Anexo buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(("SELECT anexos.* FROM anexos " +
				"WHERE idAnexo = ?");
		
			connectionDao.setInt(1, id);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Anexo> listarPorAta(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT anexos.* FROM anexos " +
				"WHERE idAta=" + String.valueOf(idAta) + " ORDER BY anexos.ordem");
		
			List<Anexo> list = new ArrayList<Anexo>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Anexo anexo) throws SQLException{
		boolean insert = (anexo.getIdAnexo() == 0);

		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO anexos(idAta, ordem, descricao, arquivo) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE anexos SET idAta=?, ordem=?, descricao=?, arquivo=? WHERE idAnexo=?");
			}
			
			connectionDao.setInt(1, anexo.getAta().getIdAta());
			connectionDao.setInt(2, anexo.getOrdem());
			connectionDao.setString(3, anexo.getDescricao());
			connectionDao.setBytes(4, anexo.getArquivo());
			
			if(!insert){
				connectionDao.setInt(5, anexo.getIdAnexo());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					anexo.setIdAnexo(rs.getInt(1));
				}
			}
			
			return anexo.getIdAnexo();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public void excluir(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			connectionDao.query().executeQuery("DELETE FROM anexos WHERE idanexo=" + String.valueOf(id));
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Anexo carregarObjeto(ResultSet rs) throws SQLException{
		Anexo anexo = new Anexo();
		
		anexo.setIdAnexo(rs.getInt("idAnexo"));
		anexo.getAta().setIdAta(rs.getInt("idAta"));
		anexo.setDescricao(rs.getString("descricao"));
		anexo.setOrdem(rs.getInt("ordem"));
		anexo.setArquivo(rs.getBytes("arquivo"));
		
		return anexo;
	}

}
