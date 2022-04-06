package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Pauta;

public class PautaDAO {
	
	public Pauta buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT * FROM pautas WHERE idPauta = ?");
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
	
	public List<Pauta> listarPorAta(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();

		try{		
			ResultSet rs = connectionDao.executeQuery("SELECT * FROM pautas WHERE idAta=" + String.valueOf(idAta) + " ORDER BY ordem");
			List<Pauta> list = new ArrayList<Pauta>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Pauta pauta) throws SQLException{
		boolean insert = (pauta.getIdPauta() == 0);

		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO pautas(idAta, ordem, titulo, descricao) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE pautas SET idAta=?, ordem=?, titulo=?, descricao=? WHERE idPauta=?");
			}
			
			connectionDao.setInt(1, pauta.getAta().getIdAta());
			connectionDao.setInt(2, pauta.getOrdem());
			connectionDao.setString(3, pauta.getTitulo());
			connectionDao.setString(4, pauta.getDescricao());
			
			if(!insert){
				connectionDao.setInt(5, pauta.getIdPauta());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					pauta.setIdPauta(rs.getInt(1));
				}
			}
			
			return pauta.getIdPauta();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public void excluir(int id) throws SQLException{		
		try{
			ConnectionDAO connectionDao = ConnectionDAO.getInstance();
			connectionDao.query().executeQuery("DELETE FROM pautas WHERE idPauta=" + String.valueOf(id));
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Pauta carregarObjeto(ResultSet rs) throws SQLException{
		Pauta pauta = new Pauta();
		
		pauta.setIdPauta(rs.getInt("idPauta"));
		pauta.getAta().setIdAta(rs.getInt("idAta"));
		pauta.setOrdem(rs.getInt("ordem"));
		pauta.setTitulo(rs.getString("titulo"));
		pauta.setDescricao(rs.getString("descricao"));
		
		return pauta;
	}

}
